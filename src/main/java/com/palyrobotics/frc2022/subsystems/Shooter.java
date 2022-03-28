package com.palyrobotics.frc2022.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;
import com.palyrobotics.frc2022.behavior.routines.miscellaneous.XboxVibrateRoutine;
import com.palyrobotics.frc2022.config.constants.ShooterConstants;
import com.palyrobotics.frc2022.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.ControllerOutput;
import com.palyrobotics.frc2022.util.dashboard.LiveGraph;
import com.palyrobotics.frc2022.vision.Limelight;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.math.filter.MedianFilter;

public class Shooter extends SubsystemBase {

	/*
	* VISION - Uses vision align to change the hood state and flywheel velocity
	* CUSTOM - Takes a custom angle and flywheel velocity
	* IDLE - Angle stays the same and flywheel speed goes to 0
	* AIMING - Moves only the angle of the hood
	* HOOD_PO - Moves only hood with PO
	* MOVING_SHOOT - Shoots while moving
	*/
	public enum State {
		VISION, CUSTOM, IDLE, TESTING, AIMING, HOOD_PO, MOVING_SHOOT
	}

	public static final int kFilterSize = 10;
	private Limelight mLimelight = Limelight.getInstance();
	private ShooterConfig mConfig = Configs.get(ShooterConfig.class);

	private static final Shooter kInstance = new Shooter();

	private final HardwareAdapter.ShooterHardware hardware = HardwareAdapter.ShooterHardware.getInstance();
	private ControllerOutput lowerFlywheelOutput = new ControllerOutput();
	private ControllerOutput upperFlywheelOutput = new ControllerOutput();
	private ControllerOutput hoodOutput = new ControllerOutput();
	private MedianFilter distanceFilter = new MedianFilter(kFilterSize);

	double flywheelLowerVelocity;
	double flywheelUpperVelocity;
	double wantedHoodAngle;
	private double targetDistanceInches;

	private boolean hasRumbled = false;

	private Shooter() {
	}

	/**
	 * Get the distance to the target using the limelight
	 */
	private double getTargetDistanceInches() {
		return targetDistanceInches = distanceFilter.calculate(mLimelight.getEstimatedDistanceInches());
	}

	/**
	 * Reads the robot state and the commands. Will update all the controller outputs.
	 *
	 * @param commands read only
	 */
	@Override
	public void update(Commands commands, RobotState state) {
		getTargetDistanceInches(); // Get the distance to target. Will set this.targetDistanceInches
		getVelocity(commands, state); // Get the wanted velocity (will update flywheelVelocity)
		getAngle(commands, state);
		getRumble(commands, state);

		LiveGraph.add("lowerDiff", Math.abs(state.shooterLowerVelocity - this.flywheelLowerVelocity));
		LiveGraph.add("upperDiff", Math.abs(state.shooterUpperVelocity - this.flywheelUpperVelocity));
		LiveGraph.add("hoodDiff", Math.abs(state.shooterHoodPosition - this.wantedHoodAngle));
		// TODO: write code for switching flywheel angle when we got more information
		state.shooterReadyToShoot = Math.abs(state.shooterLowerVelocity - this.flywheelLowerVelocity) <= ShooterConstants.maxDelta &&
				Math.abs(state.shooterUpperVelocity - this.flywheelUpperVelocity) <= ShooterConstants.maxDelta &&
				Math.abs(state.shooterHoodPosition - this.wantedHoodAngle) <= ShooterConstants.flywheelHoodMaxError;
	}

	/**
	 * Get the wanted angle of the flywheel
	 *
	 * @param commands read only
	 * @param state    read only
	 */
	public void getAngle(Commands commands, RobotState state) {
		switch (commands.getShooterWantedState()) {
			case MOVING_SHOOT:
			case VISION:
				// Use interpolated tree map to get the ideal angle given the distance we are away from the
				wantedHoodAngle = ShooterConstants.shooterMapAngle.getInterpolated(targetDistanceInches);
				hoodOutput.setTargetPositionProfiled(wantedHoodAngle, mConfig.hoodGains);
				break;
			case CUSTOM:
			case AIMING:
				wantedHoodAngle = commands.getShooterWantedAngle();
				hoodOutput.setTargetPositionProfiled(commands.getShooterWantedAngle(), mConfig.hoodGains);
				break;
			case HOOD_PO:
				hoodOutput.setPercentOutput(-0.1);
				break;
			case IDLE:
			default:
				// Keep angle the same as before
				wantedHoodAngle = 0.0;
				hoodOutput.setIdle();
				break;
		}
	}

	/**
	 * Get the wanted velocity of the flywheel
	 *
	 * @param commands read only
	 * @param state    read only
	 */
	public void getVelocity(Commands commands, RobotState state) {
		switch (commands.getShooterWantedState()) {
			case VISION:
				// Use interpolated tree maps to get the wanted velocity of BOTH upper and lower flywheels
				flywheelLowerVelocity = ShooterConstants.shooterMapLower.getInterpolated(targetDistanceInches);

				flywheelUpperVelocity = ShooterConstants.shooterMapUpper.getInterpolated(targetDistanceInches);
				lowerFlywheelOutput.setTargetVelocity(flywheelLowerVelocity, mConfig.lowerFalconGains);
				upperFlywheelOutput.setTargetVelocity(flywheelUpperVelocity, mConfig.upperFalconGains);
				break;
			case CUSTOM:
				flywheelLowerVelocity = commands.getShooterWantedVelocityLower();
				flywheelUpperVelocity = commands.getShooterWantedVelocityUpper();
				lowerFlywheelOutput.setTargetVelocity(commands.getShooterWantedVelocityLower(), mConfig.lowerFalconGains);
				upperFlywheelOutput.setTargetVelocity(commands.getShooterWantedVelocityUpper(), mConfig.upperFalconGains);
				break;
			case TESTING:
				lowerFlywheelOutput.setPercentOutput(commands.getShooterWantedBottomPO());
				upperFlywheelOutput.setPercentOutput(commands.getShooterWantedTopPO());
				break;
			case MOVING_SHOOT:
				var driveVelocity = state.driveVelocityMetersPerSecond;
				var predictionTime = 0.5;
				var newDriveVelocity = driveVelocity * predictionTime;
				var angle = state.turretYawDegrees;
				flywheelLowerVelocity = ShooterConstants.shooterMapLower.getInterpolated(targetDistanceInches);
				flywheelUpperVelocity = ShooterConstants.shooterMapUpper.getInterpolated(targetDistanceInches);
				break;
			case AIMING:
			case IDLE:
			default:
				// Set both flywheel velocities to 0, these will be set to idle later
				lowerFlywheelOutput.setIdle();
				upperFlywheelOutput.setIdle();
				break;
		}
	}

	@Override
	public void writeHardware(RobotState state) {
		hardware.lowerFalcon.setOutput(lowerFlywheelOutput);
		hardware.upperFalcon.setOutput(upperFlywheelOutput);
		hardware.hoodSpark.setOutput(hoodOutput);
	}

	@Override
	public void configureHardware() {
		hardware.upperFalcon.setSelectedSensorPosition(0);
		hardware.lowerFalcon.setSelectedSensorPosition(0);
		hardware.upperFalcon.configVoltageCompSaturation(12);
		hardware.lowerFalcon.configVoltageCompSaturation(12);
		hardware.upperFalcon.enableVoltageCompensation(true);
		hardware.lowerFalcon.enableVoltageCompensation(true);
		hardware.lowerFalcon.setNeutralMode(NeutralMode.Coast);
		hardware.upperFalcon.setNeutralMode(NeutralMode.Coast);
		hardware.hoodSpark.getEncoder().setPosition(0.0);
		hardware.hoodSpark.setInverted(true);
		hardware.hoodSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);

		hardware.upperFalcon.configSensorConversions(ShooterConstants.flywheelUpperPositionConversion,
				ShooterConstants.flywheelUpperVelocityConversion);
		hardware.lowerFalcon.configSensorConversions(ShooterConstants.flywheelLowerPositionConversion,
				ShooterConstants.flywheelLowerVelocityConversion);

		hardware.sparkEncoder.setPositionConversionFactor(ShooterConstants.flywheelHoodPositionConversion);
		hardware.sparkEncoder.setVelocityConversionFactor(ShooterConstants.flywheelHoodVelocityConversion);

		hardware.lowerFalcon.configVelocityMeasurementPeriod(SensorVelocityMeasPeriod.Period_10Ms);
		hardware.lowerFalcon.configVelocityMeasurementWindow(4);
		hardware.upperFalcon.configVelocityMeasurementPeriod(SensorVelocityMeasPeriod.Period_10Ms);
		hardware.upperFalcon.configVelocityMeasurementWindow(4);
	}

	/**
	 * Log needed stuff to live graph
	 *
	 * @param state read only
	 */
	@Override
	public void logSubsystem(RobotState state) {
		LiveGraph.add("shooterTargetDistance", targetDistanceInches == 0 ? -1 : targetDistanceInches);
		LiveGraph.add("shooterTargetVelocityLower", flywheelLowerVelocity);
		LiveGraph.add("shooterTargetVelocityUpper", flywheelUpperVelocity);
		LiveGraph.add("shooterLowerVelocity", state.shooterLowerVelocity);
		LiveGraph.add("shooterUpperVelocity", state.shooterUpperVelocity);
		LiveGraph.add("shooterHoodPosition", state.shooterHoodPosition);
		LiveGraph.add("hood current", hardware.hoodSpark.getOutputCurrent());
	}

	public static Shooter getInstance() {
		return kInstance;
	}

	/*
	* Gets whether you should rumble the xbox or not
	*/
	public void getRumble(Commands commands, RobotState state) {

		if (state.shooterReadyToShoot && mLimelight.isAligned() && commands.shooterWantedState != State.IDLE) {
			if (!hasRumbled) {
				hasRumbled = true;
				commands.addWantedRoutine(new XboxVibrateRoutine());
			} else {
				//commands.wantedRumble = false;
			}
		} else {
			hasRumbled = false;
			commands.wantedRumble = false;
		}

//		if (state.gamePeriod == RobotState.GamePeriod.AUTO) {
//			HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(commands.wantedRumble);
//		} else {
//			HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(false);
//		}

	}
}

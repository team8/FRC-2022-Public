package com.palyrobotics.frc2022.robot;

import java.util.Set;

import com.ctre.phoenix.motorcontrol.StickyFaults;
import com.ctre.phoenix.sensors.PigeonIMU.PigeonState;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.config.RobotConfig;
import com.palyrobotics.frc2022.robot.HardwareAdapter.*;
import com.palyrobotics.frc2022.subsystems.*;
import com.palyrobotics.frc2022.util.Util;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.Falcon;
import com.palyrobotics.frc2022.util.control.Spark;
import com.palyrobotics.frc2022.util.control.Talon;
import com.palyrobotics.frc2022.util.dashboard.LiveGraph;
import com.revrobotics.CANSparkMax.FaultID;
import com.revrobotics.ColorMatch;

import edu.wpi.first.wpilibj.DriverStation;

public class HardwareReader {

	private static final String kLoggerTag = Util.classToJsonName(HardwareReader.class);
	private static final int kYawIndex = 0, kYawAngularVelocityIndex = 2;
	private final RobotConfig mRobotConfig = Configs.get(RobotConfig.class);
	/**
	 * A REV Color Match object is used to register and detect known colors. This can be calibrated
	 * ahead of time or during operation. This object uses euclidean distance to estimate the closest
	 * match with a given confidence range.
	 */
	private final ColorMatch mColorMatcher = new ColorMatch();
	private final double[] mGyroAngles = new double[3], mGyroAngularVelocities = new double[3];
	private final double[] mTurretGyroAngles = new double[3], mTurretGyroAngularVelocities = new double[3];

	public HardwareReader() {
	}

	/**
	 * Takes all of the sensor data from the hardware, and unwraps it into the current
	 * {@link RobotState}.
	 */
	void updateState(Set<SubsystemBase> enabledSubsystems, RobotState state) {
		readGameAndFieldState(state);
		Robot.mDebugger.addPoint("readGameAndFieldState");
		if (enabledSubsystems.contains(Drive.getInstance())) readDriveState(state);
		if (enabledSubsystems.contains(Turret.getInstance())) readTurretState(state);
		if (enabledSubsystems.contains(Shooter.getInstance())) readShooterState(state);
		if (enabledSubsystems.contains(Climber.getInstance())) readClimberState(state);
		Robot.mDebugger.addPoint("Drive");
		if (enabledSubsystems.contains(Intake.getInstance())) readIntakeState(state);
		Robot.mDebugger.addPoint("Intake");
	}

	private void readIntakeState(RobotState state) {
		var hardware = IntakeHardware.getInstance();
		state.intakeCurrentPositionDegrees = hardware.intakeDeployEncoder.getPosition();
		state.intakeCurrentPotentiometerPosition = hardware.intakePotentiometer.getValue() - 2048;
		//System.out.println(state.intakeCurrentPotentiometerPosition);
	}

	private void readGameAndFieldState(RobotState state) {
		//state.gameData = DriverStation.getGameSpecificMessage();
	}

	private void readDriveState(RobotState state) {
		var hardware = DriveHardware.getInstance();
		/* Gyro */
		state.driveIsGyroReady = hardware.gyro.getState() == PigeonState.Ready;
		if (state.driveIsGyroReady) {
			hardware.gyro.getYawPitchRoll(mGyroAngles);
			state.driveYawDegrees = mGyroAngles[kYawIndex];
			hardware.gyro.getRawGyro(mGyroAngularVelocities);
			state.driveYawAngularVelocityDegrees = mGyroAngularVelocities[kYawAngularVelocityIndex];
		}
		/* Falcons */
		state.driveLeftVelocity = hardware.leftMasterFalcon.getConvertedVelocity();
		state.driveRightVelocity = hardware.rightMasterFalcon.getConvertedVelocity();
		state.driveLeftPosition = hardware.leftMasterFalcon.getConvertedPosition();
		state.driveRightPosition = hardware.rightMasterFalcon.getConvertedPosition();
//		LiveGraph.add("x", state.drivePoseMeters.getTranslation().getX());
//		LiveGraph.add("y", state.drivePoseMeters.getTranslation().getY());
//		LiveGraph.add("leftPosition", state.driveLeftPosition);
//		LiveGraph.add("rightPosition", state.driveRightPosition);
		/* Odometry */
		state.updateOdometry(state.driveYawDegrees, state.driveLeftPosition, state.driveRightPosition);
//		LiveGraph.add("driveLeftPosition", state.driveLeftPosition);

		LiveGraph.add("driveLeftVelocity", state.driveLeftVelocity);
//		LiveGraph.add("driveRightPosition", state.driveRightPosition);
		LiveGraph.add("driveRightVelocity", state.driveRightVelocity);
		LiveGraph.add("driveYaw", state.driveYawDegrees);
		LiveGraph.add("drive yaw angular vel", state.driveYawAngularVelocityDegrees);
//		LiveGraph.add("driveRightPercentOutput", hardware.rightMasterFalcon.getMotorOutputPercent());
//		LiveGraph.add("driveLeftPercentOutput", hardware.leftMasterFalcon.getMotorOutputPercent());
		hardware.falcons.forEach(this::checkFalconFaults);
	}

	private void readTurretState(RobotState state) {
		var hardware = TurretHardware.getInstance();
		state.turretYawDegrees = hardware.sparkEncoder.getPosition();
		state.turretYawAngularVelocity = hardware.sparkEncoder.getVelocity();
		state.turretCurrentPotentiometerPosition = hardware.turretPotentiometer.getValue() - 2048;
		LiveGraph.add("turret potentiometerPose", state.turretCurrentPotentiometerPosition);
		LiveGraph.add("turretOdomSinceVisionX", state.turretPoseSinceLastVision.getX());
		LiveGraph.add("turretOdomSinceVisionY", state.turretPoseSinceLastVision.getY());

		//	System.out.println("turret potentiometer position" + state.turretCurrentPotentiometerPosition);
	}

	private void readShooterState(RobotState state) {
		var hardware = ShooterHardware.getInstance();

		state.shooterLowerVelocity = hardware.lowerFalcon.getConvertedVelocity();
		state.shooterUpperVelocity = hardware.upperFalcon.getConvertedVelocity();
		state.shooterHoodPosition = hardware.sparkEncoder.getPosition();
//		state.shooterHoodVelocity = hardware.sparkEncoder.getVelocity();

		LiveGraph.add("hoodVelocity", hardware.sparkEncoder.getVelocity());
	}

	private void readClimberState(RobotState state) {
		var hardware = ClimberHardware.getInstance();
		state.telescopeArmLeftPosition = hardware.telescopeArmLeftEncoder.getPosition();
		state.telescopeArmRightPosition = hardware.telescopeArmRightEncoder.getPosition();
		/*TODO: MAKE SURE RIGHT SIDE IS MASTER*/
		state.swingerArmRightPosition = hardware.swingerArmMasterEncoder.getPosition();
		state.swingerArmLeftPosition = hardware.swingerArmSlaveEncoder.getPosition();

		LiveGraph.add("left position", state.telescopeArmLeftPosition);
		LiveGraph.add("right position", state.telescopeArmRightPosition);

	}

	private void checkSparkFaults(Spark spark) {
		if (mRobotConfig.checkFaults) {
			boolean wasAnyFault = false;
			for (var value : FaultID.values()) {
				boolean isFaulted = spark.getStickyFault(value);
				if (isFaulted) {
					Log.error(kLoggerTag, String.format("Spark %d fault: %s", spark.getDeviceId(), value));
					wasAnyFault = true;
				}
			}
			if (wasAnyFault) {
				spark.clearFaults();
			}
		}
	}

	private void checkTalonFaults(Talon talon) {
		if (mRobotConfig.checkFaults) {
			var faults = new StickyFaults();
			talon.getStickyFaults(faults);
			if (faults.hasAnyFault()) {
				Log.error(kLoggerTag, String.format("%s faults: %s", talon.getName(), faults));
				talon.clearStickyFaults();
			}
		}
	}

	private void checkFalconFaults(Falcon falcon) {
		if (mRobotConfig.checkFaults) {
			var faults = new StickyFaults();
			falcon.getStickyFaults(faults);
			if (faults.hasAnyFault()) {
				Log.error(kLoggerTag, String.format("%s faults: %s", falcon.getName(), faults));
				falcon.clearStickyFaults();
			}
		}
	}
}

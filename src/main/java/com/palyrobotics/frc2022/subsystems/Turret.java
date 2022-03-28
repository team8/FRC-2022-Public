package com.palyrobotics.frc2022.subsystems;

import static com.palyrobotics.frc2022.robot.HardwareWriter.*;

import com.palyrobotics.frc2022.config.constants.TurretConstants;
import com.palyrobotics.frc2022.config.subsystem.TurretConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.ControllerOutput;
import com.palyrobotics.frc2022.vision.Limelight;

import edu.wpi.first.math.filter.MedianFilter;
import edu.wpi.first.math.util.Units;

public class Turret extends SubsystemBase {

	private final static Turret sInstance = new Turret();

	public static final int kFilterSize = 3;
	private final Limelight mLimelight = Limelight.getInstance();
	private TurretConfig mTurretConfig = Configs.get(TurretConfig.class);
	private MedianFilter mVisionTargetFilter = new MedianFilter(kFilterSize);
	boolean hasSeenTarget = false;
	private double lastGyroAngleSeen = 0;

	private ControllerOutput mTurretOutput = new ControllerOutput();

	/*
	* SET_ANGLE: turns to a custom angle
	* IDLE: doesn't do anything
	* TARGETING_VISION: Target the vision target utilizing the limelight
	* MANUAL_TURNING: turns with controller input
	* RE_ZERO: Re-zeroes the turret and then sets to idle in one frame
	* ODOM: Odometry based turret movement for testing
	*/
	public enum TurretState {
		SET_ANGLE, IDLE, TARGETING_VISION, MANUAL_TURNING, RE_ZERO, ODOM
	}

	private Turret() {
	}

	@Override
	public void update(Commands commands, RobotState state) {
		double wantedAngle;
		if (mLimelight.isTargetFound()) {
			state.resetTurretLastVisionOdometry(Units.inchesToMeters(mLimelight.getEstimatedDistanceInches()));
		}
		state.updateTurretLastVisionOdometry(state.driveYawDegrees, state.driveLeftPosition, state.driveRightPosition);
		// Get angle from wanted state
		switch (commands.turretWantedState) {
			case TARGETING_VISION:
				double gyroYawDegrees = state.turretYawDegrees;
				if (mLimelight.isTargetFound()) {
					if (Math.abs(mLimelight.getYawToTarget()) < 2.0) {
						hasSeenTarget = true;
						lastGyroAngleSeen = state.driveYawDegrees;
					}
					double visionYawToTargetDegrees = mLimelight.getYawToTarget();
					wantedAngle = mVisionTargetFilter.calculate(gyroYawDegrees - visionYawToTargetDegrees);

					var latency = (mLimelight.getPipelineLatency()) / 1000.0;
					var distance = state.driveVelocityMetersPerSecond * 39.3701 * latency;
					var limelightDistance = mLimelight.getEstimatedDistanceInches();
					var latencyDistance = Math.sqrt(distance * distance + limelightDistance * limelightDistance -
							2 * distance * limelightDistance * Math.cos(Math.toRadians(state.turretYawDegrees)));
					var b = Math.toDegrees(Math.asin((Math.sin(Math.toRadians(state.turretYawDegrees)) * limelightDistance) / latencyDistance));
					b = 180 - b;
					var latencyAngle = 180 - b - state.turretYawDegrees;

					wantedAngle += latencyAngle;
				} else {
					if (hasSeenTarget) {
						double odomAngle = Math.atan2(state.turretPoseSinceLastVision.getY(), state.turretPoseSinceLastVision.getX()) * 180.0 / Math.PI;
						wantedAngle = (odomAngle) - (state.driveYawDegrees - lastGyroAngleSeen);
						break;
					} else {
						wantedAngle = 0.0;
					}
				}
				break;
			case ODOM:
				double odomAngle = Math.atan2(state.turretPoseSinceLastVision.getY(), state.turretPoseSinceLastVision.getX()) * 180.0 / Math.PI;
				wantedAngle = (odomAngle) + (state.driveYawDegrees - lastGyroAngleSeen);
				break;
			case MANUAL_TURNING:
				wantedAngle = state.turretYawDegrees + commands.turretManualWantedVelocity * mTurretConfig.manualMaxAngularVelocity;
				hasSeenTarget = false;
				break;
			case SET_ANGLE:
				// Custom angle
				wantedAngle = commands.turretCustomAngle;
				hasSeenTarget = false;
				break;
			case RE_ZERO:
				//💀
				double potentiometerDiff = TurretConstants.turretPotentiometerZero - state.turretCurrentPotentiometerPosition;
				double degreesToTurn = potentiometerDiff * (1.0 / TurretConstants.turretPotentiometerTicksInFullRot) * 360.0;
				HardwareAdapter.TurretHardware.getInstance().sparkEncoder.setPosition(-degreesToTurn);
				wantedAngle = state.turretYawDegrees;
				commands.turretWantedState = TurretState.IDLE;
				break;
			case IDLE:
			default:
				wantedAngle = state.turretYawDegrees;
				break;
		}
		double wantedSetpoint = setAngle(state, wantedAngle);
		mTurretOutput.setTargetPositionProfiled(wantedSetpoint, mTurretConfig.targetingGains);
	}

	//takes any wanted angle and then normalizes and sets it such that it is usable by the turret. Will deal with turret soft-stops and also generate the shortest path to the
	private double setAngle(RobotState state, double angle) {
		//normalize the angle
		double normAngle = angle - (360.0 * Math.round(angle / 360.0));
		//check if the angle is outside the turret bounds and set to bounds if that is the case
		if ((normAngle < -TurretConstants.maxTurretTravel + TurretConstants.maxTurretTravelPadding) && (TurretConstants.maxTurretTravel * 2) < 360.0) {
			return -TurretConstants.maxTurretTravel + TurretConstants.maxTurretTravelPadding;
		}
		if ((normAngle > TurretConstants.maxTurretTravel - TurretConstants.maxTurretTravelPadding) && (TurretConstants.maxTurretTravel * 2) < 360.0) {
			return TurretConstants.maxTurretTravel - TurretConstants.maxTurretTravelPadding;
		}

		//normalize angle from [-MAX,MAX]
		angle = angle >= 0 ? (angle - Math.ceil((angle - (TurretConstants.maxTurretTravel - TurretConstants.maxTurretTravelPadding)) / 360.0) * 360.0) : (angle - Math.floor((angle - (-TurretConstants.maxTurretTravel + TurretConstants.maxTurretTravelPadding)) / 360.0) * 360.0);

		double dCW; // calculate the distance to spin CW
		double dCCW; // calculate the distance to spin CCW

		double current = state.turretYawDegrees;

		//always positive
		double normalized = Math.floor(((360.0 + normAngle) - current) / 360.0) * 360.0;
		dCW = (360.0 + normAngle) - current - normalized;
		//always negative
		dCCW = normAngle - current - normalized;
		double endPointCW = current + dCW;
		double endPointCCW = current + dCCW;

		//if either endpoint is outside of bounds, return the other
		if (endPointCW > TurretConstants.maxTurretTravel - TurretConstants.maxTurretTravelPadding) {
			return endPointCCW;
		}
		if (endPointCCW < -TurretConstants.maxTurretTravel + TurretConstants.maxTurretTravelPadding) {
			return endPointCW;
		}
		//if either distance is shorter return that one... :D
		if (dCW <= dCCW) {
			return endPointCW;
		} else {
			return endPointCCW;
		}
	}

	@Override
	public void writeHardware(RobotState state) {
		HardwareAdapter.TurretHardware hardware = HardwareAdapter.TurretHardware.getInstance();
		hardware.spark.setOutput(mTurretOutput);
	}

	@Override
	public void configureHardware() {
		HardwareAdapter.TurretHardware hardware = HardwareAdapter.TurretHardware.getInstance();
		hardware.spark.restoreFactoryDefaults();
		hardware.spark.setInverted(false);
		/*TODO: set current limit and voltage stuff if necessary*/
		hardware.sparkEncoder.setPositionConversionFactor(TurretConstants.turretPositionConversionFactor);
		hardware.sparkEncoder.setVelocityConversionFactor(TurretConstants.turretVelocityConversionFactor);
		resetTurretSensors();
	}

	public static Turret getInstance() {
		return sInstance;
	}
}

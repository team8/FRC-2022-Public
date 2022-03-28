package com.palyrobotics.frc2022.robot;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.config.constants.DriveConstants;
import com.palyrobotics.frc2022.util.Util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;

/**
 * Holds the current physical state of the robot from our sensors.
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotState {

	public enum GamePeriod {
		AUTO, TELEOP, TESTING, DISABLED
	}

	public static final String kLoggerTag = Util.classToJsonName(RobotState.class);

	/* Drive */
	private final DifferentialDriveOdometry driveOdometry = new DifferentialDriveOdometry(new Rotation2d());
	public double driveYawDegrees, driveYawAngularVelocityDegrees;
	public boolean driveIsQuickTurning, driveIsSlowTurning, driveIsSlowMoving;
	public double driveLeftVelocity, driveRightVelocity, driveLeftPosition, driveRightPosition;
	public Pose2d drivePoseMeters = new Pose2d();
	public double driveVelocityMetersPerSecond;
	public boolean driveIsGyroReady;
	/* Turret */
	public double turretYawDegrees, turretYawAngularVelocity;
	public int turretCurrentPotentiometerPosition;
	public double turretLastVisionDriveLeftPosition, turretLastVisionDriveRightPosition, turretLastVisionGyroYaw;
	public Pose2d turretPoseSinceLastVision = new Pose2d();
	public Pose2d poseInitVision = new Pose2d();
	public final DifferentialDriveOdometry turretLastVisionDriveOdometry = new DifferentialDriveOdometry(new Rotation2d());

	/* Shooter */
	public double shooterUpperVelocity;
	public double shooterLowerVelocity;
	public boolean shooterReadyToShoot;
	public double shooterHoodPosition;

	/* Intake */
	public double intakeCurrentPotentiometerPosition;
	public double intakeCurrentPositionDegrees;

	/* Climber */
	public boolean climberIsSwingerLocked;
	public boolean climberIsTelescopeLocked;
	public double telescopeArmLeftPosition;
	public double telescopeArmRightPosition;
	public double swingerArmRightPosition;
	public double swingerArmLeftPosition;

	/* Game and Field */
	public GamePeriod gamePeriod = GamePeriod.DISABLED;
	public String gameData;

	public void resetOdometry(Pose2d pose) {
		driveOdometry.resetPosition(pose, pose.getRotation());
		drivePoseMeters = driveOdometry.getPoseMeters();
		driveVelocityMetersPerSecond = 0.0;
		Log.info(kLoggerTag, String.format("Odometry reset to: %s", pose));
	}

	public void updateOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		drivePoseMeters = driveOdometry.update(Rotation2d.fromDegrees(yawDegrees), leftMeters, rightMeters);
		ChassisSpeeds speeds = DriveConstants.kKinematics.toChassisSpeeds(new DifferentialDriveWheelSpeeds(driveLeftVelocity, driveRightVelocity));
		driveVelocityMetersPerSecond = Math.sqrt(Math.pow(speeds.vxMetersPerSecond, 2) + Math.pow(speeds.vyMetersPerSecond, 2));
	}

	public void resetTurretLastVisionOdometry(double distToTarget) {
		//reset based on turret yaw degrees to get a cartesian position away from the robot
		Pose2d pose = new Pose2d(distToTarget * Math.cos(Math.toRadians(turretYawDegrees)), distToTarget * Math.sin(Math.toRadians(turretYawDegrees)), new Rotation2d());
		poseInitVision = pose;
		turretPoseSinceLastVision = pose;
		turretLastVisionDriveOdometry.resetPosition(new Pose2d(), new Rotation2d());
		turretLastVisionGyroYaw = driveYawDegrees;
		turretLastVisionDriveLeftPosition = driveLeftPosition;
		turretLastVisionDriveRightPosition = driveRightPosition;
	}

	public void updateTurretLastVisionOdometry(double yawDegrees, double leftMeters, double rightMeters) {
		Pose2d toAdd = turretLastVisionDriveOdometry.update(Rotation2d.fromDegrees(yawDegrees - turretLastVisionGyroYaw), (leftMeters - turretLastVisionDriveLeftPosition), (rightMeters - turretLastVisionDriveRightPosition));
		turretPoseSinceLastVision = new Pose2d(poseInitVision.getX() + toAdd.getX(), poseInitVision.getY() + toAdd.getY(), new Rotation2d());
	}
}

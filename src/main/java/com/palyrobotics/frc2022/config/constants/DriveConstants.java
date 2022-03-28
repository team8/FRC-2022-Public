package com.palyrobotics.frc2022.config.constants;

import com.palyrobotics.frc2022.config.subsystem.DriveConfig;
import com.palyrobotics.frc2022.util.config.Configs;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.math.util.Units;

public class DriveConstants {

	public static final double kMaxVoltage = 12.0;

	/**
	 * Path following constants
	 */
	//TODO: update this, update pathweaver constants
	public static final double kWheelDiameterInches = 4.0,
			kTrackWidthMeters = 0.71247;

	public static final DifferentialDriveKinematics kKinematics = new DifferentialDriveKinematics(DriveConstants.kTrackWidthMeters);
	public static final double kS = 0.64546, kV = 2.3685, kA = 0.36689; // Tuned 3/1/22
	public static final SimpleMotorFeedforward kFeedForward = new SimpleMotorFeedforward(kS, kV, kA);
	public static final DifferentialDriveVoltageConstraint kVoltageConstraints = new DifferentialDriveVoltageConstraint(kFeedForward, kKinematics, kMaxVoltage);

	/**
	 * Unit Conversions
	 */
	public static final double kDriveMetersPerTick = (1.0 / 2048.0) * (1.0 / 7.0) * Units.inchesToMeters(kWheelDiameterInches) * Math.PI,
			kDriveMetersPerSecondPerTickPer100Ms = kDriveMetersPerTick * 10;
	/**
	 * Cheesy Drive Constants
	 */
	public static final double kDeadBand = 0.05;

	private static DriveConfig kConfig = Configs.get(DriveConfig.class);

	private DriveConstants() {
	}

	/**
	 * @return Copy of the standard trajectory configuration. Can be modified safely.
	 */
	public static TrajectoryConfig getTrajectoryConfig(double maxPathVelocityMetersPerSecond, double maxPathAccelerationMetersPerSecondSquared) {
		return new TrajectoryConfig(maxPathVelocityMetersPerSecond, maxPathAccelerationMetersPerSecondSquared)
				.setKinematics(kKinematics)
				.addConstraint(kVoltageConstraints);
	}

	public static double calculateTimeToFinishTurn(double currentYawDegrees, double targetYawDegrees) {
		return new TrapezoidProfile(
				new TrapezoidProfile.Constraints(kConfig.turnGains.velocity, kConfig.turnGains.acceleration),
				new TrapezoidProfile.State(targetYawDegrees, 0.0), new TrapezoidProfile.State(currentYawDegrees, 0.0)).totalTime();
	}
}

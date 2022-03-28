package com.palyrobotics.frc2022.config.constants;

import com.palyrobotics.frc2022.util.InterpolatingDoubleTreeMap;

public class ShooterConstants {

	public static final double flywheelUpperPositionConversion = 1;
	public static final double flywheelUpperVelocityConversion = (1.0 / 2048.0) * (4.0 / 9.0) * (1000.0 / 100.0) * (60.0);
	public static final double flywheelLowerPositionConversion = 1;
	public static final double flywheelLowerVelocityConversion = (1.0 / 2048.0) * (4.0 / 9.0) * (1000.0 / 100.0) * (60.0);
	public static final double flywheelHoodPositionConversion = -(1.0 / 504.0) * (360.0);
	public static final double flywheelHoodVelocityConversion = -(1.0 / 504.0) * (360.0) * (1.0 / 60.0);
	public static final double flywheelHoodMaxError = 0.5;

	public static final double maxDelta = 100.0; // Maximum error in the flywheel velocities

	public static final double ballShootTime = 0.8; // Time to shoot one ball

	public static InterpolatingDoubleTreeMap shooterMapLower = new InterpolatingDoubleTreeMap();
	public static InterpolatingDoubleTreeMap shooterMapUpper = new InterpolatingDoubleTreeMap();
	public static InterpolatingDoubleTreeMap shooterMapAngle = new InterpolatingDoubleTreeMap();

	static {
		shooterMapLower.put(82.5, 1300.0);
		shooterMapUpper.put(82.5, 1450.0);
		shooterMapAngle.put(82.5, 30.0);

		shooterMapLower.put(104.5, 1450.0);
		shooterMapUpper.put(104.5, 1550.0);
		shooterMapAngle.put(104.5, 35.5);

		shooterMapLower.put(128.7, 1500.0);
		shooterMapUpper.put(128.7, 1650.0);
		shooterMapAngle.put(128.7, 41.0);

		shooterMapLower.put(161.0, 1600.0);
		shooterMapUpper.put(161.0, 1850.0);
		shooterMapAngle.put(161.0, 45.5);

		shooterMapLower.put(183.5, 1800.0);
		shooterMapUpper.put(148.5, 2100.0);
		shooterMapAngle.put(148.5, 45.0);
	}
}

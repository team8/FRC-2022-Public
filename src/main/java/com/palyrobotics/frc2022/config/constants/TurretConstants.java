package com.palyrobotics.frc2022.config.constants;

public class TurretConstants {

	//(1/rotations per revolution) (get number of turret revolutions done) * 360 (degrees) * 1/60 (is in rpm)
	public static double turretVelocityConversionFactor = 0.13043;
	//(1/rotations per revolution) (get number of turret revolutions done) * 360 (degrees)
	public static double turretPositionConversionFactor = 7.82608;
	public static double maxTurretTravel = 85.0;
	public static double maxTurretTravelPadding = 0.0;
	public static int turretPotentiometerZero = -751;
	//total potentiometer ticks in 360 degrees
	public static int turretPotentiometerTicksInFullRot = 5160;
}

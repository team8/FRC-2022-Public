package com.palyrobotics.frc2022.config.constants;

public class IntakeConstants {

	//155 rotations of motor to one revolution of the intake deploy mech
	public static final double intakePositionConversionFactor = (1 / 225.0) * 360.0;
	//rpm
	public static final double intakeVelocityConversionFactor = (1 / 225.0) * 360.0 * (1.0 / 60.0);
	public static final double intakePotentiometerTicksInFullRotation = 819.2;
	public static final double intakePotentiometerZero = 0;
	public static final double intakeMinimumRangeOfMotion = -2.0;
	//1 radian
	public static final double intakeStowedPositionDegrees = 30.0; //57.2958
	public static final double intakeIntakingPositionDegrees = 0.0;
}

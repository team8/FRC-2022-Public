package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2022.util.control.Gains;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class DriveConfig extends SubsystemConfigBase {

	public Gains velocityGains;
	public ProfiledGains profiledVelocityGains, turnGains;
	public double turnGainsS;
	public double quickStopWeight, quickTurnScalar, quickStopDeadBand, quickStopScalar, slowTurnScalar, slowMoveScalar, turnSensitivity,
			lowNegativeInertiaThreshold, lowNegativeInertiaFarScalar, lowNegativeInertiaCloseScalar,
			lowNegativeInertiaTurnScalar, wheelNonLinearity;
	public int nonlinearPasses;
	public double pathVelocityMetersPerSecond, pathAccelerationMetersPerSecondSquared;
	public double allowableYawErrorDegrees;
}

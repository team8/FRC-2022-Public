package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.ConfigBase;
import com.palyrobotics.frc2022.util.control.Gains;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class ShooterConfig extends ConfigBase {

	public Gains upperFalconGains, lowerFalconGains;
	public ProfiledGains hoodGains;
	public double shooterCustomHoodAngle;
	public double shooterCustomLowerFlywheelSpeed;
	public double shooterCustomHighFlywheelSpeed;
	public double minimumHoodResetDelta;
}

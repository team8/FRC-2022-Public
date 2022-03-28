package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class TurretConfig extends SubsystemConfigBase {

	public ProfiledGains targetingGains;
	public double yawError, gyroYawVelocityCompensationFactor;
	public double manualMaxAngularVelocity;
}

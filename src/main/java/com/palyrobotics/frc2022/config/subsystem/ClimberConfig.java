package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class ClimberConfig extends SubsystemConfigBase {

	public double rezeroOutput;
	public double minDeltaRezero;

	public double positionControlDistance;
	public double telescopeSwingerHeightDifference;
	public double telescopeHookDownSlightDistance;
	public double acceptableArmPositionError;

	public ProfiledGains armVelocityGains;
	public ProfiledGains swingerVelocityGains;
}

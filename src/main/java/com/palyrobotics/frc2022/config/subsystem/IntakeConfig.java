package com.palyrobotics.frc2022.config.subsystem;

import com.palyrobotics.frc2022.util.config.SubsystemConfigBase;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

public class IntakeConfig extends SubsystemConfigBase {

	public double intakePercentOutput;
	public double kVoltageCompensation;
	public double intakeDeployTolerance;
	public double feedForwardPO;
	public double timeUntilReset;
	public ProfiledGains deployGains;
}

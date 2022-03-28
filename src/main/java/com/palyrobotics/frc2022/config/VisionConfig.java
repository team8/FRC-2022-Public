package com.palyrobotics.frc2022.config;

import com.palyrobotics.frc2022.util.config.ConfigBase;
import com.palyrobotics.frc2022.util.control.Gains;
import com.palyrobotics.frc2022.util.control.ProfiledGains;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class VisionConfig extends ConfigBase {

	public ProfiledGains profiledGains;
	public Gains preciseGains;
	public double acceptableYawError, alignSwitchYawAngleMin;

	public Gains oneTimesZoomGains, twoTimesZoomGains;
}

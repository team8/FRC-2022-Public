package com.palyrobotics.frc2022.config;

import java.util.List;

import com.palyrobotics.frc2022.util.config.ConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class RobotConfig extends ConfigBase {

	public boolean coastDriveWhenDisabled, disableHardwareUpdates, enableVisionWhenDisabled, checkFaults;
	public int visionPipelineWhenDisabled;

	// Useful for testing at lower speeds
	public double motorOutputMultiplier;

	public List<String> enabledServices, enabledSubsystems;
}

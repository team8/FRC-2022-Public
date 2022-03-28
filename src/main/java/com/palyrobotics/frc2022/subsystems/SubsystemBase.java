package com.palyrobotics.frc2022.subsystems;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.Util;

public abstract class SubsystemBase {

	private final String mName;

	protected SubsystemBase() {
		mName = Util.classToJsonName(getClass());
	}

	public abstract void update(@ReadOnly Commands commands, @ReadOnly RobotState state);

	public abstract void writeHardware(@ReadOnly RobotState state);

	public abstract void configureHardware();

	public void logSubsystem(RobotState state) {

	}

	@Override
	public String toString() {
		return getName();
	}

	public String getName() {
		return mName;
	}
}

package com.palyrobotics.frc2022.util.service;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.Util;

public interface RobotService {

	default void start() {
	}

	default void update(@ReadOnly RobotState state, @ReadOnly Commands commands) {
	}

	default String getConfigName() {
		return Util.classToJsonName(getClass());
	}
}

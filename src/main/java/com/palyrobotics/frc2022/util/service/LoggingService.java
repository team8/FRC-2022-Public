package com.palyrobotics.frc2022.util.service;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.Loggable;

public class LoggingService implements RobotService {

	@Override
	public void update(RobotState state, Commands commands) {
		for (Loggable l : HardwareAdapter.loggables) {
			l.log();
		}
	}
}

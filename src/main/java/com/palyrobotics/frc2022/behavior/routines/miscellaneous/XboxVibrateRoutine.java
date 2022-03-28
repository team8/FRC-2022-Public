package com.palyrobotics.frc2022.behavior.routines.miscellaneous;

import com.palyrobotics.frc2022.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;

public class XboxVibrateRoutine extends TimedRoutine {

	public XboxVibrateRoutine() {
		super(2.0);
	}

	@Override
	public void update(Commands commands, @ReadOnly RobotState state) {
		commands.wantedRumble = true;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.wantedRumble = false;
	}
}

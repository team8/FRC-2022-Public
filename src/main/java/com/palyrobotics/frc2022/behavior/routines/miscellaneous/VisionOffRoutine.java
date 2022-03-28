package com.palyrobotics.frc2022.behavior.routines.miscellaneous;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.OneUpdateRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

public class VisionOffRoutine extends OneUpdateRoutineBase {

	@Override
	protected void updateOnce(Commands commands, RobotState state) {
		commands.visionWanted = false;
	}

	/**
	 * Store subsystems which are required by this routine, preventing routines from overlapping
	 */
	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return null;
	}
}

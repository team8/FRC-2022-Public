package com.palyrobotics.frc2022.behavior.routines;

import java.util.List;

import com.palyrobotics.frc2022.behavior.ParallelRoutine;
import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;

public class ParallelRaceRoutine extends ParallelRoutine {

	public ParallelRaceRoutine(RoutineBase... routines) {
		super(routines);
	}

	public ParallelRaceRoutine(List<RoutineBase> routines) {
		super(routines);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mRoutines.stream().anyMatch(RoutineBase::isFinished);
	}
}

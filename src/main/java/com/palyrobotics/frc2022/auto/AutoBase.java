package com.palyrobotics.frc2022.auto;

import java.util.List;

import com.palyrobotics.frc2022.behavior.RoutineBase;

import edu.wpi.first.math.trajectory.Trajectory;

public abstract class AutoBase {

	public abstract RoutineBase getRoutine();

	public List<Trajectory.State> getFullTrajectoryStates() {
		// TODO: implement
		RoutineBase routine = getRoutine();
		return null;
	}

}

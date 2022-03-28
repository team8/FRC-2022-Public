package com.palyrobotics.frc2022.behavior.routines;

import java.util.HashSet;
import java.util.Set;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

public abstract class TimedRoutine extends RoutineBase {

	protected final Timer mTimer = new Timer();
	protected double mTimeout;

	/**
	 * Routine that waits the specified amount of time. Does not require any subsystems.
	 */
	public TimedRoutine(double durationSeconds) {
		mTimeout = durationSeconds;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		mTimer.start();
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return mTimer.hasElapsed(mTimeout);
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return new HashSet<>();
	}
}

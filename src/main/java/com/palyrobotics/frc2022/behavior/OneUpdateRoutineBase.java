package com.palyrobotics.frc2022.behavior;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;

/**
 * Completes after one update cycle has fully elapsed, not immediately. It is possible that
 * {@link Commands} can intend to modify {@link RobotState}, so if our routine finished immediately,
 * it may not update data in {@link RobotState} which other routines depend on.
 */
public abstract class OneUpdateRoutineBase extends RoutineBase {

	private int mUpdateCount;

	@Override
	protected final void update(Commands commands, @ReadOnly RobotState state) {
		mUpdateCount++;
		updateOnce(commands, state);
	}

	protected abstract void updateOnce(Commands commands, @ReadOnly RobotState state);

	@Override
	public final boolean checkFinished(@ReadOnly RobotState state) {
		return mUpdateCount > 1;
	}
}

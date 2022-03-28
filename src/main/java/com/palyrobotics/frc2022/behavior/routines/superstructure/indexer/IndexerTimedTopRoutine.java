package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.*;
import com.palyrobotics.frc2022.subsystems.*;

public class IndexerTimedTopRoutine extends TimeoutRoutineBase {

	private boolean mReverse;

	public IndexerTimedTopRoutine(double duration, boolean reverse) {
		super(duration);
		this.mReverse = reverse;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return false;
	}

	public void update(RobotState state, Commands commands) {
		if (!mReverse) {
			commands.indexerTopWantedState = Indexer.State.INDEX;
		} else {
			commands.indexerTopWantedState = Indexer.State.REVERSE;
		}
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerTopWantedState = Indexer.State.IDLE;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}

}

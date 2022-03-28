package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

public class IndexerTimedBottomRoutine extends TimeoutRoutineBase {

	private boolean mReverse;

	public IndexerTimedBottomRoutine(double timeout, boolean reverse) {
		super(timeout);
		mReverse = reverse;
	}

	@Override
	public void update(Commands commands, RobotState state) {
		if (!mReverse) {
			commands.indexerBottomWantedState = Indexer.State.INDEX;
		} else {
			commands.indexerBottomWantedState = Indexer.State.REVERSE;
		}
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.indexerBottomWantedState = Indexer.State.IDLE;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return false;
	}
}

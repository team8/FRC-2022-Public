package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;

public class IndexerFeedAllRoutine extends TimeoutRoutineBase {

	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private boolean mReverse;

	public IndexerFeedAllRoutine(double duration, boolean reverse) {
		super(duration);
		mReverse = reverse;
	}

	@Override
	public void stop(Commands commands, RobotState robotState) {
		commands.setIndexerIdle();
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return false;
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		if (!mReverse) {
			commands.indexerBottomWantedState = Indexer.State.INDEX;
			commands.indexerTopWantedState = Indexer.State.INDEX;
		} else {
			commands.indexerTopWantedState = Indexer.State.REVERSE;
			commands.indexerBottomWantedState = Indexer.State.REVERSE;
		}
	}

	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}
}

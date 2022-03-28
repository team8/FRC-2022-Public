package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;

public class IndexerFeedAllPORoutine extends IndexerFeedAllRoutine {

	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private boolean mReverse;
	private double mPO;

	public IndexerFeedAllPORoutine(double PO, double duration, boolean reverse) {
		super(duration, reverse);
		this.mPO = PO;
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		if (!mReverse) {
			commands.indexerBottomWantedState = Indexer.State.MANUAL;
			commands.indexerTopWantedState = Indexer.State.MANUAL;
			commands.setIndexerTopManual(mPO);
			commands.setIndexerBottomManual(mPO);
		} else {
			commands.indexerTopWantedState = Indexer.State.MANUAL;
			commands.indexerBottomWantedState = Indexer.State.MANUAL;
			commands.setIndexerTopManual(-mPO);
			commands.setIndexerBottomManual(-mPO);
		}
	}

	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}
}

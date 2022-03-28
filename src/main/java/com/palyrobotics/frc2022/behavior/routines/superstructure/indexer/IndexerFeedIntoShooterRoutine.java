package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.constants.ShooterConstants;
import com.palyrobotics.frc2022.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;

public class IndexerFeedIntoShooterRoutine extends TimeoutRoutineBase {

	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);
	private final double kStartTime = 0.3;

	public IndexerFeedIntoShooterRoutine(double duration) {
		super(duration);
	}

	@Override
	public void stop(Commands commands, RobotState state) {
		commands.setIndexerIdle();
		commands.setShooterIdle();
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return mTimer.hasElapsed(2 * ShooterConstants.ballShootTime + kStartTime);
	}

	@Override
	public void update(Commands commands, RobotState robotState) {
		commands.indexerTopWantedState = Indexer.State.INDEX;
		commands.indexerBottomWantedState = Indexer.State.INDEX;
		commands.setShooterVision();
	}

	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}
}

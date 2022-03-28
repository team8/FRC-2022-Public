package com.palyrobotics.frc2022.behavior.routines.superstructure.indexer;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.OneUpdateRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

public abstract class IndexerIdleRoutine extends OneUpdateRoutineBase {

	@Override
	protected void updateOnce(Commands commands, RobotState state) {
		commands.setIndexerIdle();
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Indexer.class);
	}

}

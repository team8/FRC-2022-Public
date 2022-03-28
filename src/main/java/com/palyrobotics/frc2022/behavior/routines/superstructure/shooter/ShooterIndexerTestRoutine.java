package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import com.palyrobotics.frc2022.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;

public class ShooterIndexerTestRoutine extends TimedRoutine {

	/**
	 * Routine that waits the specified amount of time. Does not require any subsystems.
	 */
	public ShooterIndexerTestRoutine() {
		super(10);
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setShooterCustomPO(0.7, 0.7);
		commands.indexerTopWantedState = Indexer.State.INDEX;
		commands.indexerBottomWantedState = Indexer.State.INDEX;
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
		commands.setShooterIdle();
	}
}

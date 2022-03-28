package com.palyrobotics.frc2022.behavior.routines.superstructure.intake;

import java.util.*;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.Intake;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

/*
 * This routine will run the intake for durationSeconds at the Percent Output of
 * intakePercentOutput. After durationSeconds has passed, the intake will stop.
 */
public class IntakeBallRoutine extends TimeoutRoutineBase {

	public IntakeBallRoutine(double durationSeconds) {
		super(durationSeconds);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	public void start(Commands commands, RobotState state) {
		mTimer.start();
		commands.intakeJustDeployed = true;
		commands.intakeDeployWantedState = Intake.DeployState.DEPLOYED;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setIntakeBalls();
		commands.indexerBottomWantedState = Indexer.State.INDEX;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.intakeWantedState = Intake.State.OFF;
		commands.intakeDeployWantedState = Intake.DeployState.STOWED;
		commands.intakeJustStowed = true;
		commands.indexerBottomWantedState = Indexer.State.IDLE;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Intake.class, Indexer.class);
	}
}

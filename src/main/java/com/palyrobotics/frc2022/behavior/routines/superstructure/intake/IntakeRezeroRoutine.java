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
 * This routine will stow the intake, then set it to idle.
 */
public class IntakeRezeroRoutine extends TimeoutRoutineBase {

	boolean canFinish = false;

	public IntakeRezeroRoutine(double duration) {
		super(duration);
	}

	public IntakeRezeroRoutine() {
		super(0.3);

	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.intakeDeployWantedState = Intake.DeployState.RE_ZERO;
//		System.out.println("rezero start");
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return false;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
//		System.out.println("rezero routine");
//		if (commands.intakeDeployWantedState == Intake.DeployState.STOWED) {
//			canFinish = true;
//		}

	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		//commands.intakeDeployWantedState = Intake.DeployState.STOWED;
		commands.intakeWantedState = Intake.State.OFF;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Intake.class, Indexer.class);
	}
}

package com.palyrobotics.frc2022.behavior.routines.superstructure.turret;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Turret;

public class TurretReZeroRoutineIntoManualTurn extends TimeoutRoutineBase {

	public TurretReZeroRoutineIntoManualTurn(double duration) {
		super(duration);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.setTurretRezero();
	}

	@Override
	public void update(Commands commands, RobotState state) {
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return false;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.turretWantedState = Turret.TurretState.MANUAL_TURNING;
	}
}

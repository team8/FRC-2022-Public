package com.palyrobotics.frc2022.behavior.routines.superstructure.turret;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.subsystems.Turret;

public class TurretReZeroRoutine extends TimeoutRoutineBase {

	boolean hasStartedMove = false;

	public TurretReZeroRoutine() {
		super(2);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.setTurretRezero();
	}

	@Override
	public void update(Commands commands, RobotState state) {
		if (commands.turretWantedState.equals(Turret.TurretState.IDLE)) {
			hasStartedMove = true;
			commands.setTurretCustomAngle(0.0);
		}
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		if (hasStartedMove) {
			return Math.abs(state.turretYawDegrees) < 0.25;
		}
		return false;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.turretWantedState = Turret.TurretState.IDLE;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Turret.class);
	}
}

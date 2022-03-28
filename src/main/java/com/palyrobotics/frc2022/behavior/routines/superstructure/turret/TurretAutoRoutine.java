package com.palyrobotics.frc2022.behavior.routines.superstructure.turret;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.subsystems.Turret;

public class TurretAutoRoutine extends RoutineBase {

	boolean hasStartedMove = false;

	public TurretAutoRoutine() {
	}

	@Override
	public void update(Commands commands, RobotState state) {
		commands.turretWantedState = Turret.TurretState.TARGETING_VISION;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.turretWantedState = Turret.TurretState.IDLE;
	}

	@Override
	public boolean checkFinished(RobotState state) {
		return false;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Turret.class);
	}
}

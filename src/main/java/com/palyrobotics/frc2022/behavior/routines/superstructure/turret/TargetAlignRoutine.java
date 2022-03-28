package com.palyrobotics.frc2022.behavior.routines.superstructure.turret;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Turret;

public class TargetAlignRoutine extends TimeoutRoutineBase {

	public TargetAlignRoutine(double duration) {
		super(duration);
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return false;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.visionWanted = true;
	}

	@Override
	public void update(Commands commands, RobotState state) {
		commands.turretWantedState = Turret.TurretState.TARGETING_VISION;
	}

	@Override
	public void stop(Commands commands, @ReadOnly RobotState state) {
		commands.turretWantedState = Turret.TurretState.IDLE;
	}
}

package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Shooter;

public class SpeedShooter extends TimeoutRoutineBase {

	public SpeedShooter() {
		super(2);
	}

	@Override
	public void start(Commands commands, RobotState state) {
		commands.shooterWantedState = Shooter.State.VISION;
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return state.shooterReadyToShoot;
	}
}

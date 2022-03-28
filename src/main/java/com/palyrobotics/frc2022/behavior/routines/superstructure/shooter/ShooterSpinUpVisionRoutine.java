package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

// for autos
public class ShooterSpinUpVisionRoutine extends TimedRoutine {

	public ShooterSpinUpVisionRoutine(double duration) {
		super(duration);
	}

	@Override
	protected void update(Commands commands, RobotState state) {

	}

	@Override
	public void start(Commands commands, RobotState state) {
		commands.shooterWantedState = Shooter.State.VISION;
		commands.visionWanted = true;
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
		super.stop(commands, state);
	}

	@Override
	public boolean checkFinished(RobotState state) {
		return false;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Shooter.class);
	}
}

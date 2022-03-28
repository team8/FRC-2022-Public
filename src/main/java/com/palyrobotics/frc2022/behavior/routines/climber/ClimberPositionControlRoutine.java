package com.palyrobotics.frc2022.behavior.routines.climber;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Climber;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;

/*
 * moves the climber telescoping arm to a certain position
 */

public class ClimberPositionControlRoutine extends RoutineBase {

	private double wantedClimberPosition;
	private final ClimberConfig mConfig = Configs.get(ClimberConfig.class);

	public ClimberPositionControlRoutine(double distance) {
		super();
		wantedClimberPosition = distance;
	}

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {
		commands.climberArmWantedPosition = wantedClimberPosition;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.climberArmWantedState = Climber.ArmState.POSITION_CONTROL;
	}

	@Override
	public boolean checkFinished(RobotState state) {
		return Math.abs(wantedClimberPosition - state.telescopeArmLeftPosition) <= mConfig.acceptableArmPositionError &&
				Math.abs(wantedClimberPosition - state.telescopeArmRightPosition) <= mConfig.acceptableArmPositionError;
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.climberArmWantedState = Climber.ArmState.LOCKED;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Climber.class);
	}
}

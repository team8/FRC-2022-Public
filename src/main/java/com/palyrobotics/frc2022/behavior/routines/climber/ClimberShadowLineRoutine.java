package com.palyrobotics.frc2022.behavior.routines.climber;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Climber;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.util.Color;

/*
 * Drives the bot forward and aligns to the shadow line so the climber is directly below the mid
 * bar. status: UNFINISHED
 */

public class ClimberShadowLineRoutine extends RoutineBase {

	@Override
	protected void start(Commands commands, @ReadOnly RobotState state) {

	}

	@Override
	public boolean checkFinished(RobotState state) {
//		return shadowLineDetected(state.detectedRGBValues);
		return true;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class, Climber.class);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
	}

	private boolean shadowLineDetected(Color detectedColor) {
//		int kAcceptableRGBError = new ClimberConstants().kAcceptableRGBError;
//		if (detectedColor.red < kAcceptableRGBError && detectedColor.green < kAcceptableRGBError && detectedColor.blue < kAcceptableRGBError) {
//			return true;
//		}
//		return false;
		return true;
	}
}

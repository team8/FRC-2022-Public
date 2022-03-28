package com.palyrobotics.frc2022.behavior.routines.drive;

import java.util.function.Predicate;

import com.palyrobotics.frc2022.behavior.routines.PredicateWaitRoutine;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;

import edu.wpi.first.math.geometry.Pose2d;

public class PredicateDriveWaitRoutine extends PredicateWaitRoutine<Pose2d> {

	public PredicateDriveWaitRoutine(Predicate<Pose2d> predicate) {
		super(predicate);
	}

	@Override
	public boolean checkFinished(@ReadOnly RobotState state) {
		return super.mPredicate.test(state.drivePoseMeters);
	}
}

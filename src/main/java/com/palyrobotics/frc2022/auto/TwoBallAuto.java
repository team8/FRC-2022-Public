package com.palyrobotics.frc2022.auto;

import java.io.IOException;

import com.palyrobotics.frc2022.behavior.ParallelRoutine;
import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.SequentialRoutine;
import com.palyrobotics.frc2022.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.indexer.IndexerFeedAllRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.intake.IntakeBallRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.intake.IntakeRezeroRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ResetHoodRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ShooterSpinUpVisionRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TargetAlignRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TurretReZeroRoutine;
import com.palyrobotics.frc2022.util.Util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.util.Units;

public class TwoBallAuto extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		try {
			Pose2d initPose = Util.newWaypoint(Units.metersToInches(7.594), Units.metersToInches(1.624), Units.radiansToDegrees(-1.5291537476963104));
			var setInitialOdometry = new DriveSetOdometryRoutine(Units.metersToInches(initPose.getX()), Units.metersToInches(initPose.getY()), initPose.getRotation().getDegrees());
//			var setInitialOdometry = new DriveSetOdometryRoutine(Units.metersToInches(7.594), Units.metersToInches(1.624), Units.radiansToDegrees(-1.5291537476963104));

			var firstBall = new ParallelRaceRoutine(
					new ShooterSpinUpVisionRoutine(50.0),
					new IntakeBallRoutine(50.0),
					new SequentialRoutine(
							new DrivePathPremadeRoutine("output/SecondBall.wpilib.json"),
							new IndexerFeedAllRoutine(10.0, false)));
			return new SequentialRoutine(setInitialOdometry, new ParallelRoutine(new ResetHoodRoutine(), new IntakeRezeroRoutine()), new ParallelRaceRoutine(new SequentialRoutine(new TurretReZeroRoutine(), new TargetAlignRoutine(40)), firstBall));
		} catch (IOException e) {
			return new DriveSetOdometryRoutine(0, 0, 0);
		}
	}
}

package com.palyrobotics.frc2022.auto;

import java.io.IOException;

import com.palyrobotics.frc2022.behavior.ParallelRoutine;
import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.SequentialRoutine;
import com.palyrobotics.frc2022.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.indexer.IndexerFeedIntoShooterRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.intake.IntakeBallRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.intake.IntakeRezeroRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ResetHoodRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ShooterSpinUpVisionRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TargetAlignRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TurretReZeroRoutine;
import com.palyrobotics.frc2022.util.Util;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.util.Units;

public class PranavRobbie4BallAuto extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		try {
			Pose2d initPose = Util.newWaypoint(Units.metersToInches(7.594), Units.metersToInches(1.624), Units.radiansToDegrees(-1.5291537476963104));
			var setInitialOdometry = new DriveSetOdometryRoutine(Units.metersToInches(initPose.getX()), Units.metersToInches(initPose.getY()), initPose.getRotation().getDegrees());
//			var setInitialOdometry = new DriveSetOdometryRoutine(Units.metersToInches(7.594), Units.metersToInches(1.624), Units.radiansToDegrees(-1.5291537476963104));

			var firstBall = new SequentialRoutine(
					new ParallelRaceRoutine(
							new ShooterSpinUpVisionRoutine(50.0),
							new IntakeBallRoutine(50.0),
							new DrivePathPremadeRoutine("output/SecondBall.wpilib.json")),
					new IndexerFeedIntoShooterRoutine(2));

//			var secondBall = new ParallelRaceRoutine(
//					new SequentialRoutine(
//							new DrivePathPremadeRoutine("output/ThirdBall.wpilib.json")),
//					new IntakeBallRoutine(50.0),
//					new ShooterSpinUpVisionRoutine(50.0));
			var fourthBall = new ParallelRaceRoutine(
					new SequentialRoutine(
							new DrivePathPremadeRoutine("output/ReversePath.wpilib.json"),
							new DrivePathPremadeRoutine("output/ThirdBall_FourthBall.wpilib.json"),
							new DrivePathPremadeRoutine("output/TerminalReverse.wpilib.json"),
							new IndexerFeedIntoShooterRoutine(10)),
					new IntakeBallRoutine(50.0),
					new ShooterSpinUpVisionRoutine(50.0));

			return new SequentialRoutine(setInitialOdometry, new ParallelRoutine(new IntakeRezeroRoutine(), new ResetHoodRoutine()), new ParallelRaceRoutine(new SequentialRoutine(new TurretReZeroRoutine(), new TargetAlignRoutine(40)), new SequentialRoutine(firstBall, fourthBall)));
		} catch (IOException e) {
			return new DriveSetOdometryRoutine(0, 0, 0);
		}
	}
}

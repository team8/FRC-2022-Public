package com.palyrobotics.frc2022.auto;

import java.io.IOException;

import com.palyrobotics.frc2022.behavior.ParallelRoutine;
import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.SequentialRoutine;
import com.palyrobotics.frc2022.behavior.routines.ParallelRaceRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.indexer.IndexerFeedAllRoutine;
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

public class FourBallAuto extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		try {
			Pose2d initPose = Util.newWaypoint(Units.metersToInches(6.411), Units.metersToInches(2.688), Units.radiansToDegrees(-2.711895182175719));
			var setInitialOdometry = new DriveSetOdometryRoutine(Units.metersToInches(initPose.getX()), Units.metersToInches(initPose.getY()), initPose.getRotation().getDegrees());

			var firstBall = new SequentialRoutine(
					new ParallelRaceRoutine(
							new ShooterSpinUpVisionRoutine(50.0),
							new IntakeBallRoutine(55.0),
							new DrivePathPremadeRoutine("output/Adit4ballpt1.wpilib.json")),
					new IndexerFeedIntoShooterRoutine(12.0));

			var secondBall = new SequentialRoutine(
					new ParallelRaceRoutine(
							new SequentialRoutine(
									new DrivePathPremadeRoutine("output/Adit4ballpt2.wpilib.json"),
									new DrivePathPremadeRoutine("output/AditBackup.wpilib.json"),
									new IndexerFeedAllRoutine(3, false)),
							new IntakeBallRoutine(55.0),
							new ShooterSpinUpVisionRoutine(50.0)),
					new IndexerFeedIntoShooterRoutine(12.0));
			return new SequentialRoutine(setInitialOdometry, new ParallelRoutine(new ResetHoodRoutine(), new IntakeRezeroRoutine()), new ParallelRaceRoutine(new SequentialRoutine(new TurretReZeroRoutine(), new TargetAlignRoutine(40)), new SequentialRoutine(firstBall, secondBall)));

//			var out = new ParallelRaceRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(Units.metersToInches(6.62), Units.metersToInches(2.710), Units.radiansToDegrees(-2.761086276477431)),
//					//TODO: replace line below with turret
//					new ParallelRaceRoutine(
//							new DrivePathPremadeRoutine("output/Adit4ballpt1.wpilib.json"), new IntakeBallRoutine(10)),
//					new ShootAllVisionRoutine(),
//					new ParallelRoutine(
//							new DrivePathPremadeRoutine("output/Adit4ballpt2.wpilib.json"), new IntakeBallRoutine(4)),
//					new DrivePathPremadeRoutine("output/AditBackup.wpilib.json"),
//					new ShootAllVisionRoutine()),
//					new TargetAlignRoutine(20));
//			return out;
		} catch (IOException e) {
			return new DriveSetOdometryRoutine(0, 0, 0);
		}
	}
}

package com.palyrobotics.frc2022.behavior.routines.drive;

import com.palyrobotics.frc2022.behavior.RoutineManager;
import com.palyrobotics.frc2022.util.Util;

public class RelativeDrivePathRoutine extends DrivePathRoutine {

	public static final String kLoggerTag = Util.classToJsonName(RoutineManager.class);
//TODO: implement

//	public RelativeDrivePathRoutine() {
//		super(Util.newWaypoint(-0.3, 0, 0));
//	}
//
//	@Override
//	public void generateTrajectory(Pose2d startingPose) {
//		Log.debug(kLoggerTag, String.format("making trajectory: %s%n", "generateTrajectory"));
//		System.out.println("mtrajectorymade");
//		Transform2d transform = new Transform2d(Util.newWaypoint(0, 0, 0), startingPose);
//		startingPose = Util.newWaypoint(0, 0, 0);
//		super.generateTrajectory(startingPose);
//		mTrajectory = mTrajectory.transformBy(transform);
//	}
}

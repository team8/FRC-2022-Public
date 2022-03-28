package com.palyrobotics.frc2022.auto;

import java.io.IOException;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.SequentialRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathPremadeRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;

import edu.wpi.first.math.util.Units;

public class DrivePremadeAutoTest extends AutoBase {

	@Override
	public RoutineBase getRoutine() {
		try {
			SequentialRoutine out = new SequentialRoutine(
					new DriveSetOdometryRoutine(Units.metersToInches(6.280935670501099), Units.metersToInches(5.861366157863388), Units.radiansToDegrees(2.4805494847391065)),
					new DrivePathPremadeRoutine("output/Shoot1Get1.wpilib.json"));
			return out;
		} catch (IOException e) {
			return new DriveSetOdometryRoutine(0, 0, 0);
		}
	}
}

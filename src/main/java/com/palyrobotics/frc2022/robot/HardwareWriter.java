package com.palyrobotics.frc2022.robot;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.sensors.PigeonIMU;
import com.ctre.phoenix.sensors.PigeonIMU_StatusFrame;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.util.Util;
import com.palyrobotics.frc2022.util.control.Spark;

import edu.wpi.first.math.geometry.Pose2d;

public class HardwareWriter {

	// Blocks config calls for specified timeout
	public static final int kTimeoutMs = 150;
	// Different from slot index.
	// 0 for Primary closed-loop. 1 for auxiliary closed-loop.
	public static final int kPidIndex = 0;
	private static final String kLoggerTag = Util.classToJsonName(HardwareWriter.class);
	public static final double kVoltageCompensation = 12.0;
	public static final SupplyCurrentLimitConfiguration k30AmpCurrentLimitConfiguration = new SupplyCurrentLimitConfiguration(
			true, 30.0, 35.0, 1.0);

	void configureMiscellaneousHardware() {
		var hardware = HardwareAdapter.MiscellaneousHardware.getInstance();
		hardware.pdp.clearStickyFaults();
//		hardware.compressor.clearAllPCMStickyFaults();
	}

	public static void resetDriveSensors(Pose2d pose) {
		double heading = pose.getRotation().getDegrees();
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.gyro.setYaw(heading, kTimeoutMs);
		hardware.leftMasterFalcon.setSelectedSensorPosition(0);
		hardware.rightMasterFalcon.setSelectedSensorPosition(0);
		Log.info(kLoggerTag, String.format("Drive sensors reset, gyro heading: %s", heading));
	}

	public static void resetTurretSensors() {
		HardwareAdapter.TurretHardware hardware = HardwareAdapter.TurretHardware.getInstance();
		hardware.sparkEncoder.setPosition(0.0);
	}

	void setDriveNeutralMode(NeutralMode neutralMode) {
		var hardware = HardwareAdapter.DriveHardware.getInstance();
		hardware.falcons.forEach(f -> f.setNeutralMode(neutralMode));
	}

	public static void handleReset(Spark spark) {
//		if (spark.getStickyFault(FaultID.kHasReset)) {
//			spark.clearFaults();
//			Log.error(kLoggerTag, String.format("%s spark reset", spark.getName()));
//		}
	}

	public static void setPigeonStatusFramePeriods(PigeonIMU gyro) {
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.CondStatus_9_SixDeg_YPR, 5, kTimeoutMs);
		gyro.setStatusFramePeriod(PigeonIMU_StatusFrame.BiasedStatus_2_Gyro, 5, kTimeoutMs);
	}

	public static void handleReset(PigeonIMU pigeon) {
		if (pigeon.hasResetOccurred()) {
			Log.error(kLoggerTag, "Pigeon reset");
			setPigeonStatusFramePeriods(pigeon);
		}
	}
}

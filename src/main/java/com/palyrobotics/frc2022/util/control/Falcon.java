package com.palyrobotics.frc2022.util.control;

import static com.palyrobotics.frc2022.robot.HardwareWriter.kTimeoutMs;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.util.Loggable;
import com.palyrobotics.frc2022.util.control.Talon.BaseTalonController;
import com.palyrobotics.frc2022.util.csvlogger.CSVWriter;

public class Falcon extends TalonFX implements Controller, Loggable {

	static class FalconController extends BaseTalonController<Falcon> {

		protected FalconController(Falcon falcon) {
			super(falcon);
		}
	}

	private final FalconController mController = new FalconController(this);
	private final String mName;

	public Falcon(int deviceId, String name) {
		super(deviceId);
		mName = name;
		clearStickyFaults(kTimeoutMs);
	}

	public boolean setOutput(ControllerOutput output) {
		return mController.setOutput(output);
	}

	/**
	 * @param positionConversion Units per native encoder ticks
	 * @param velocityConversion Velocity units per native encoder ticks per 100ms
	 */
	public void configSensorConversions(double positionConversion, double velocityConversion) {
		mController.mPositionConversion = positionConversion;
		mController.mVelocityConversion = velocityConversion;
	}

	/**
	 * @see Talon#handleReset()
	 */
	public void handleReset() {
		if (hasResetOccurred()) {
			Log.error("reset", String.format("%s reset", mController.getName()));
			mController.updateFrameTimings();
		}
	}

	public void setFrameTimings(int controlFrameMs, int statusFrameMs) {
		mController.configFrameTimings(controlFrameMs, statusFrameMs);
	}

	public String getName() {
		return String.format("(Falcon #%d), %s", getDeviceID(), mName);
	}

	@Override
	public void log() {
		CSVWriter.addData(getName() + " Current", getSupplyCurrent());
		CSVWriter.addData(getName() + " PO", getMotorOutputPercent());
		CSVWriter.addData(getName() + " Voltage", getMotorOutputVoltage());
		CSVWriter.addData(getName() + " Temp", getTemperature());
		CSVWriter.addData(getName() + " Velocity", getConvertedVelocity());
	}

	public double getConvertedPosition() {
		return getSelectedSensorPosition() * mController.mPositionConversion;
	}

	public double getConvertedVelocity() {
		return getSelectedSensorVelocity() * mController.mVelocityConversion;
	}
}

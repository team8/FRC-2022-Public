package com.palyrobotics.frc2022.util.input;

import com.palyrobotics.frc2022.util.Loggable;
import com.palyrobotics.frc2022.util.csvlogger.CSVWriter;

public class XboxController extends edu.wpi.first.wpilibj.XboxController implements Loggable {

	private static final double kTriggerThreshold = 0.8;
	public static final int kPovUp = 0, kPovRight = 90, kPovDown = 180, kPovLeft = 270;
	private int mLastPOV = -1;
	private boolean mLastLeftTrigger, mLastRightTrigger;

	public XboxController(int port) {
		super(port);
	}

	public void updateLastInputs() {
		mLastPOV = getPOV();
		mLastLeftTrigger = getLeftTrigger();
		mLastRightTrigger = getRightTrigger();
	}

	public void clearLastInputs() {
		mLastPOV = -1;
		mLastLeftTrigger = mLastRightTrigger = false;
	}

	public boolean getDPadRightPressed() {
		return getPOV() != mLastPOV && getDPadRight();
	}

	public boolean getDPadRightReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovRight;
	}

	public boolean getDPadRight() {
		return getPOV() == kPovRight;
	}

	public boolean getDPadUpPressed() {
		return getPOV() != mLastPOV && getDPadUp();
	}

	public boolean getDPadUPReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovUp;
	}

	public boolean getDPadUp() {
		return getPOV() == kPovUp;
	}

	public boolean getDPadDownPressed() {
		return getPOV() != mLastPOV && getDPadDown();
	}

	public boolean getDPadDownReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovDown;
	}

	public boolean getDPadDown() {
		return getPOV() == kPovDown;
	}

	public boolean getDPadLeftPressed() {
		return getPOV() != mLastPOV && getDPadLeft();
	}

	public boolean getDPadLeftReleased() {
		return getPOV() != mLastPOV && mLastPOV == kPovLeft;
	}

	public boolean getDPadLeft() {
		return getPOV() == kPovLeft;
	}

	public boolean getLeftTrigger() {
		return getLeftTriggerAxis() > kTriggerThreshold;
	}

	public boolean getRightTrigger() {
		return getRightTriggerAxis() > kTriggerThreshold;
	}

	public boolean getLeftTriggerPressed() {
		return mLastLeftTrigger != getLeftTrigger() && getLeftTrigger();
	}

	public boolean getRightTriggerPressed() {
		return mLastRightTrigger != getRightTrigger() && getRightTrigger();
	}

	public boolean getLeftTriggerReleased() {
		return getLeftTrigger() != mLastLeftTrigger && mLastLeftTrigger;
	}

	public boolean getRightTriggerReleased() {
		return getRightTrigger() != mLastRightTrigger && mLastRightTrigger;
	}

	public boolean getWindowButtonPressed() {
		return getRawButtonPressed(7);
	}

	public boolean getMenuButtonPressed() {
		return getRawButtonPressed(8);
	}

	public void setRumble(boolean isOn) {
		setRumble(RumbleType.kRightRumble, isOn ? 1.0 : 0.0);
		setRumble(RumbleType.kLeftRumble, isOn ? 1.0 : 0.0);
	}

	@Override
	public void log() {
		CSVWriter.addData("XBox A", getAButton());
		CSVWriter.addData("XBox B", getBButton());
		CSVWriter.addData("XBox Y", getYButton());
		CSVWriter.addData("XBox X", getXButton());
		CSVWriter.addData("XBox LTrigger", getLeftTrigger());
		CSVWriter.addData("XBox RTrigger", getRightTrigger());
		CSVWriter.addData("XBox LBumper", getLeftBumper());
		CSVWriter.addData("XBox RBumper", getRightBumper());
		CSVWriter.addData("XBox DPad Up", getDPadUp());
		CSVWriter.addData("XBox DPad Down", getDPadDown());
		CSVWriter.addData("XBox DPad Left", getDPadLeft());
		CSVWriter.addData("XBox DPad Right", getDPadRight());
		CSVWriter.addData("XBox Left X", getLeftX());
		CSVWriter.addData("XBox Left Y", getLeftY());
		CSVWriter.addData("XBox Right X", getRightX());
		CSVWriter.addData("XBox Right Y", getRightY());
	}
}

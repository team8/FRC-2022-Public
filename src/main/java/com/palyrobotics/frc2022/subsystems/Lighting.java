package com.palyrobotics.frc2022.subsystems;

import java.util.*;

import com.palyrobotics.frc2022.config.subsystem.LightingConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.controllers.lighting.*;
import com.palyrobotics.frc2022.util.Color;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.LightingOutputs;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Timer;

public class Lighting extends SubsystemBase {

	public enum State {
		OFF, IDLE, INIT, DISABLE, DO_NOTHING, ALIGNED, HOOD_RESET
	}

	public abstract static class LEDController {

		protected static final double kZeroSpeed = 1e-4;

		protected LightingOutputs mOutputs = new LightingOutputs();
		protected Timer mTimer = new Timer();

		protected int mStartIndex;
		protected int mLastIndex;
		protected double mSpeed;
		protected int kPriority;

		protected LEDController(int startIndex, int lastIndex) {
			for (var i = 0; i <= Math.abs(lastIndex - startIndex); i++) {
				mOutputs.lightingOutput.add(new Color.HSV());
			}
			mTimer.reset();
		}

		@Override
		public boolean equals(Object object) {
			if (object instanceof LEDController) {
				LEDController otherController = (LEDController) object;
				return otherController.mStartIndex == this.mStartIndex && otherController.mLastIndex == this.mLastIndex && this.getClass().getName().equals(otherController.getClass().getName());
			}
			return false;
		}

		public final LightingOutputs update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
			updateSignal(commands, state);
			return mOutputs;
		}

		public abstract void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state);

		public boolean checkFinished() {
			return false;
		}
	}

	private static Lighting sInstance = new Lighting();
	private LightingConfig mConfig = Configs.get(LightingConfig.class);
	private AddressableLEDBuffer mOutputBuffer = new AddressableLEDBuffer(mConfig.ledCount);
	private State mState;
	private HardwareAdapter.LightingHardware hardware = HardwareAdapter.LightingHardware.getInstance();
	private PriorityQueue<LEDController> mLEDControllers = new PriorityQueue<>(1, Comparator.comparingInt(o -> o.kPriority));

	private Lighting() {

	}

	public static Lighting getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		State wantedState = commands.lightingWantedState;
		if (RobotController.getBatteryVoltage() < mConfig.minVoltageToFunction) wantedState = State.OFF;
		boolean isNewState = mState != wantedState;
		mState = wantedState;
		if (isNewState) {
			switch (mState) {
				case OFF:
					resetLedStrip();
					mLEDControllers.clear();
					break;
				case IDLE:
					break;
				case INIT:
					break;
				case HOOD_RESET:
					addToControllers(new DivergingBandsController(mConfig.totalSegmentFirstIndex, mConfig.totalSegmentLastIndex, Color.HSV.kAqua, Color.HSV.kWhite, 8, 0.03, 2.0));
					break;
				case ALIGNED:
					addToControllers(new FadeInFadeOutController(mConfig.totalSegmentFirstIndex, mConfig.totalSegmentLastIndex, Color.HSV.kGreen, 0.4, 1.0));
					break;
				case DISABLE:
					resetLedStrip();
					mLEDControllers.clear();
					addToControllers(new RainbowController(mConfig.totalSegmentFirstIndex, mConfig.totalSegmentMidLastIndex, 5, 175));
					addToControllers(new RainbowController(mConfig.totalSegmentMidFirstIndex, mConfig.totalSegmentLastIndex, 5, 175));
					break;
			}
		}

		resetLedStrip();
		if (mLEDControllers.removeIf(LEDController::checkFinished)) {
			mState = State.DO_NOTHING;
		}

		for (LEDController ledController : mLEDControllers) {
			LightingOutputs controllerOutput = ledController.update(commands, state);
			for (int i = 0; i < controllerOutput.lightingOutput.size(); i++) {
				Color.HSV hsvValue = controllerOutput.lightingOutput.get(i);
				mOutputBuffer.setHSV(i + ledController.mStartIndex, hsvValue.getH(), hsvValue.getS(), Math.min(hsvValue.getV(), mConfig.maximumBrightness));
			}
		}
	}

	@Override
	public void writeHardware(RobotState state) {
		hardware.ledStrip.setData(mOutputBuffer);
	}

	@Override
	public void configureHardware() {
		hardware.ledStrip.setLength(Configs.get(LightingConfig.class).ledCount);
		hardware.ledStrip.start();
		hardware.ledStrip.setData(mOutputBuffer);
	}

	private void addToControllers(LEDController controller) {
		mLEDControllers.removeIf(controller::equals);
		mLEDControllers.add(controller);
	}

	private void resetLedStrip() {
		for (int i = 0; i < mOutputBuffer.getLength(); i++) {
			mOutputBuffer.setRGB(i, 0, 0, 0);
		}
	}
}

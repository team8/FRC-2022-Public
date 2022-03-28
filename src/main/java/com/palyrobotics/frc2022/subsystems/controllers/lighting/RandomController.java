package com.palyrobotics.frc2022.subsystems.controllers.lighting;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Lighting;

public class RandomController extends Lighting.LEDController {

	private int mCurrentLedIndex;
	boolean hasRandomized = false;

	/**
	 * Animation in which entire h component in hsv is displayed.
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 * @param speed      Speed of animation
	 */

	public RandomController(int startIndex, int lastIndex, double speed) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		mCurrentLedIndex = mStartIndex;
		mSpeed = speed == 0 ? kZeroSpeed : speed;
		kPriority = 4;
		mTimer.start();
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1) {
			for (int i = mStartIndex; i < mLastIndex; i++) {
				int h = (int) (Math.random() * 255);
				int s = (int) (Math.random() * 50) + 200;
				int v = (int) (Math.random() * 100) + 50;
				mOutputs.lightingOutput.get(i).setHSV(h, s, v);
			}
			hasRandomized = true;
		}

	}

}

package com.palyrobotics.frc2022.subsystems.controllers.lighting;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Lighting;

public class InitSequenceController extends Lighting.LEDController {

	private int mCurrentLedIndex;
	private int mOffset;

	/**
	 * Animation in which entire h component in hsv is displayed.
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 * @param speed      Speed of animation
	 */

	public InitSequenceController(int startIndex, int lastIndex, double speed) {
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
		if (Math.round(mTimer.get() / mSpeed) % 2 == 1 && mCurrentLedIndex < mLastIndex) {
			int h = (int) ((mCurrentLedIndex - mStartIndex) * (mCurrentLedIndex) +
					5);
			while (h > 255) {
				h -= 255;
			}
			mOutputs.lightingOutput.get(mCurrentLedIndex - mStartIndex).setHSV(h, 247, 100);
			mCurrentLedIndex += 1;
			mTimer.reset();
		} else if (mCurrentLedIndex >= mLastIndex && Math.round(mTimer.get() / mSpeed) % 2 == 1) {
			mOffset++;
			for (int i = mStartIndex; i < mLastIndex; i++) {
				int h = (int) ((mCurrentLedIndex - (mStartIndex + mOffset)) * (mCurrentLedIndex) +
						5);
				while (h > 255) {
					h -= 255;
				}
				mOutputs.lightingOutput.get(i).setHSV(h, 247, 100);
			}
			mTimer.reset();
		}
	}

}

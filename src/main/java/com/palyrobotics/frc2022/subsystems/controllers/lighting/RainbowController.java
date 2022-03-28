package com.palyrobotics.frc2022.subsystems.controllers.lighting;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Lighting;

public class RainbowController extends Lighting.LEDController {

	private double mDuration = -1;

	int s = 255;
	int v = 20;
	int h;

	private int mFinalHue;
	private int mInitHue;

	/**
	 * Band color converges to center of strip
	 *
	 * @param startIndex Initial index upon which led patterns should start
	 * @param lastIndex  End index upon which led patterns should stop
	 */

	public RainbowController(int startIndex, int lastIndex, int initHue, int finalHue) {
		super(startIndex, lastIndex);
		mStartIndex = startIndex;
		mLastIndex = lastIndex;
		kPriority = 1;
		mTimer.start();
		mInitHue = initHue;
		mFinalHue = finalHue;
		h = mInitHue;
	}

	@Override
	public void updateSignal(Commands commands, RobotState state) {

		int hprime = h;
		for (int i = 0; i < mOutputs.lightingOutput.size(); i++) {
			mOutputs.lightingOutput.get(i).setHSV(hprime, s, v);
			hprime++;
			if (hprime > mFinalHue) {
				hprime = mInitHue;
			}
		}
		h++;
		if (h > mFinalHue) h = mInitHue;
	}

	@Override
	public boolean checkFinished() {
		return mDuration != -1 && mTimer.hasElapsed(mDuration);
	}
}

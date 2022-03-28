//package com.palyrobotics.frc2022.subsystems.controllers.lighting;
//
//import com.palyrobotics.frc2022.robot.Commands;
//import com.palyrobotics.frc2022.robot.RobotState;
//import com.palyrobotics.frc2022.subsystems.Lighting;
//
//public class RainbowControllerInit extends Lighting.LEDController {
//
//    private double mDuration = -1;
//
//    int s = 255;
//    int v = 20;
//    int h;
//
//    /**
//     * Band color converges to center of strip
//     *
//     * @param startIndex Initial index upon which led patterns should start
//     * @param lastIndex  End index upon which led patterns should stop
//     */
//
//    public RainbowControllerInit(int startIndex, int lastIndex) {
//        super(startIndex, lastIndex);
//        mStartIndex = startIndex;
//        mLastIndex = lastIndex;
//        kPriority = 1;
//        mTimer.start();
//        h = 5;
//    }
//
//    @Override
//    public void updateSignal(Commands commands, RobotState state) {
//
//        mOutputs.lightingOutput.get(i).getHSV()
//        int hprime = h;
//        for (int i = 0; i < mOutputs.lightingOutput.size(); i++) {
//            if (!(mOutputs.lightingOutput.get(i).getH() == 0 && mOutputs.lightingOutput.get(i).getS() == 0 && mOutputs.lightingOutput.get(i).getS() == 0)) {
//                adsfasf
//            }
//            else
//            {
//                mOutputs.lightingOutput.get(i).setHSV(hprime, s, v);
//                hprime++;
//                if (hprime > 250) {
//                    hprime = 5;
//                }
//            }
//        }
//        h++;
//        if (h > 250) h = 5;
//    }
//
//    @Override
//    public boolean checkFinished() {
//        return mDuration != -1 && mTimer.hasElapsed(mDuration);
//    }
//}

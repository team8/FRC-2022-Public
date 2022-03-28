package com.palyrobotics.frc2022.util.input;

import com.palyrobotics.frc2022.util.Loggable;
import com.palyrobotics.frc2022.util.csvlogger.CSVWriter;

public class Joystick extends edu.wpi.first.wpilibj.Joystick implements Loggable {

	public Joystick(int port) {
		super(port);
	}

	public String getName() {
		return "Joystick " + getPort();
	}

	@Override
	public void log() {
		for (int i = 0; i < getButtonCount(); i++) {
			CSVWriter.addData(getName() + " Button " + i, getRawButton(i));
		}
		for (int i = 0; i < getAxisCount(); i++) {
			CSVWriter.addData(getName() + " Axis " + i, getRawAxis(i));
		}
	}
}

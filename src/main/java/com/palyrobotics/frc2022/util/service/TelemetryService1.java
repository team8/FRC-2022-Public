package com.palyrobotics.frc2022.util.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;

public class TelemetryService1 extends ServerServiceBase1 {

	private Update mTelemetry = new Update();

	static class Update {

		RobotState state;
		Commands commands;
		Map<String, Object> arbitrary = new HashMap<>();
	}

	private static TelemetryService1 sInstance;

	public TelemetryService1() {
		sInstance = this;
	}

	@Override
	int getPort() {
		return 5807;
	}

	public static void putArbitrary(String name, Object value) {
		if (sInstance != null) {
			sInstance.mTelemetry.arbitrary.put(name, value);
		}
	}

	@Override
	public void update(@ReadOnly RobotState state, @ReadOnly Commands commands) {
		if (mServer.getConnections().length > 0) {
			mTelemetry.state = state;
			mTelemetry.commands = commands;
			try {
				String json = Configs.getMapper().writeValueAsString(mTelemetry);
				mServer.sendToAllUDP(json);
			} catch (JsonProcessingException ignored) {
			}
		}
	}
}

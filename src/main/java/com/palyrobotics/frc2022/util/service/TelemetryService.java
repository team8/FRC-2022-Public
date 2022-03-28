package com.palyrobotics.frc2022.util.service;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.http.HttpInput;
import com.palyrobotics.frc2022.util.http.LightHttpServer;

import org.json.JSONObject;

// telemetry
public class TelemetryService extends com.palyrobotics.frc2022.util.service.ServerServiceBase {

	private Update mTelemetry = new Update();

	static class Update {

		RobotState state;
		Commands commands;
		Map<String, Object> arbitrary = new HashMap<>();
	}

	private static TelemetryService sInstance;

	public TelemetryService(LightHttpServer server) {
		super(server);
		sInstance = this;
	}

	@Override
	public int getPort() {
		return 4000;
	}

	public static void putArbitrary(String name, Object value) {
		if (sInstance != null) {
			sInstance.mTelemetry.arbitrary.put(name, value);
		}
	}

	@Override
	public void start() {
		try {
			String json = Configs.getMapper().writeValueAsString(mTelemetry);
			HttpInput.getInstance().setTelemetry(new JSONObject(json));
		} catch (JsonProcessingException ignored) {
		}
	}

	@Override
	public void update(@ReadOnly RobotState state, @ReadOnly Commands commands) {
		if (mServer.getConnected()) {
			mTelemetry.state = state;
			mTelemetry.commands = commands;
			try {
				String json = Configs.getMapper().writeValueAsString(mTelemetry);
				HttpInput.getInstance().setTelemetry(new JSONObject(json));
			} catch (JsonProcessingException ignored) {
			}
		}
	}
}

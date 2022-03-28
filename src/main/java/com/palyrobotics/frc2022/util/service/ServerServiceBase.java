package com.palyrobotics.frc2022.util.service;

import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.util.http.LightHttpServer;

// has boilerplate code for starting server
public abstract class ServerServiceBase implements RobotService {

	protected String mLoggerTag = getConfigName();
	protected LightHttpServer mServer;

	public ServerServiceBase(LightHttpServer server) {
		mServer = server;
	}

	@Override
	public void start() {
		int port = getPort();
		try {
			mServer.setServer(port);
		} catch (Exception exception) {
			Log.error("Error setting server", exception);
		}
		Log.info(mLoggerTag, "Started server");
	}

	abstract int getPort();
}

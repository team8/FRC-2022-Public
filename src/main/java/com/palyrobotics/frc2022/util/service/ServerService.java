package com.palyrobotics.frc2022.util.service;

import com.palyrobotics.frc2022.util.http.LightHttpServer;

// starts the http server
public class ServerService extends ServerServiceBase implements RobotService {

	public ServerService(LightHttpServer mServer) {
		super(mServer);
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public int getPort() {
		return 4000;
	}
}

package com.palyrobotics.frc2022.util.service;

import com.esotericsoftware.minlog.Log;
import com.esotericsoftware.minlog.Log.Logger;
import com.palyrobotics.frc2022.util.http.LightHttpServer;

import edu.wpi.first.wpilibj.Timer;

// control center logging
public class NetworkLoggerService extends ServerServiceBase implements RobotService {

	private static final int kPort = 4000;
	private final Timer mTimer = new Timer();

	private Logger mLogger = new Logger() {

		@Override
		public void log(int level, String category, String message, Throwable exception) {
			if (category != null && (category.equals("kryo") || category.equals("kryo.FieldSerializerConfig"))) {
				// Kryonet can possibly log when this function is called, creating a recursive
				// loop
				return;
			}
			double timeSeconds = mTimer.get();
			long timeMilliseconds = Math.round(timeSeconds * 1000.0);
			var log = new LogEntry(timeMilliseconds, level, category, message, exception);
			if (!mServer.getConnected()) {

				// If we aren't connected, forward to system out which goes to driver station
				if (level == Log.LEVEL_ERROR) {
					System.err.println(log);
				} else {
					System.out.println(log);
				}
			} else {
				mServer.addLog(log);
			}
		}
	};

	public NetworkLoggerService(LightHttpServer mServer) {
		super(mServer);
	}

	@Override
	public int getPort() {
		return kPort;
	}

	@Override
	public void start() {
		mTimer.start();
		Log.setLogger(mLogger);
		Log.set(Log.LEVEL_TRACE);
	}
}

package com.palyrobotics.frc2022.util.service;

import java.io.File;
import java.io.IOException;

import com.esotericsoftware.minlog.Log;

// runs website on robot
public class WebService implements Runnable, RobotService {

	private Thread webThread;
	private boolean mReal;
	private Process mProcess = null;

	public WebService(boolean isReal) {
		mReal = isReal;
	}

	@Override
	public void run() {
		try {
			Log.info("Beginning Website Setup");
			if (mReal) {
				mProcess = Runtime.getRuntime().exec("python3 -m http.server", null, new File("/home/lvuser/deploy/website"));
				System.out.println("Started Robot Server");
			} else {
				mProcess = Runtime.getRuntime().exec("python3 -m http.server", null, new File(String.format("%s/src/main/deploy/website", System.getProperty("user.dir"))));
				System.out.println("Started Simulate Server");
			}
			Log.info("Finished Website Setup");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				mProcess.destroy();
			}

		});
		if (webThread == null) {
			webThread = new Thread(this, "websiteThread");
			webThread.start();
		}
	}

}

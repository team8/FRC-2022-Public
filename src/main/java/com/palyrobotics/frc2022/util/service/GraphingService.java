package com.palyrobotics.frc2022.util.service;

import com.palyrobotics.frc2022.util.dashboard.LiveGraph;
import com.palyrobotics.frc2022.util.http.LightHttpServer;

import edu.wpi.first.networktables.NetworkTable;

import org.json.JSONObject;

// control center graphing
public class GraphingService implements RobotService {

	NetworkTable liveTable = LiveGraph.getTable();
	private JSONObject jsonData = new JSONObject();
	private LightHttpServer mServer;

	public GraphingService(LightHttpServer server) {
		this.mServer = server;
	}

	@Override
	public void start() {
		Thread graphingThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					for (String s : liveTable.getKeys()) {
						jsonData.put(s, 0);
					}
					liveTable.addEntryListener(((table, key, entry, value, flags) -> {
						if (value.isBoolean()) {
							if (value.getBoolean()) {
								jsonData.put(key, 1);
							} else {
								jsonData.put(key, 0);
							}
							LiveGraph.setJSONData(jsonData);
						} else {
							jsonData.put(key, value.getDouble());
							LiveGraph.setJSONData(jsonData);
						}
						if (mServer.getConnected()) {
							mServer.addGraphPoints(jsonData);
						}
					}), 0xFF);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		graphingThread.setPriority(Thread.MIN_PRIORITY);
		graphingThread.start();
	}
}

package com.palyrobotics.frc2022.util.http;

import java.util.ArrayList;

import com.palyrobotics.frc2022.util.dashboard.LiveGraph;
import com.palyrobotics.frc2022.util.service.LogEntry;

import org.json.*;

// adds and changes information to be sent to server
public class InputThread implements Runnable {

	/*
	* add a new array for any list data you want to add
	*/

	private Thread mThread;
	private JSONObject json = new JSONObject();
	private ArrayList<LogEntry> logs = new ArrayList<>();
	private JSONArray logArray = new JSONArray();
	private JSONArray posArray = new JSONArray();
	private String dataType = null;

	private final int logSize = 50;

	@Override
	public void run() {
		logArray.clear();
		for (int i = 0; i < logs.size(); i++) {
			logArray.put(logs.get(i).toString());
		}
		/*
		* add new data to output json
		*/
		json.put("logs", logArray);
		json.put("graphData", LiveGraph.getJSONData());
		json.put("position", posArray);
		if (dataType.equals("logs")) {
			HttpInput.getInstance().setLogInput(json);
		} else if (dataType.equals("chart")) {
			HttpInput.getInstance().setChartInput(json);
		} else if (dataType.equals("position")) {
			HttpInput.getInstance().setPositionInput(json);
		}
	}

	/*
	* interface to add new data to input thread
	*/

	public void addLog(LogEntry log) {
		logs.add(log);
	}

	public void addPos(double X, double Y) {
		JSONObject tempXY = new JSONObject();
		tempXY.put("x", X);
		tempXY.put("y", Y);
		posArray.put(tempXY);
	}

	public void start(String sendType) {
		dataType = sendType;
		if (mThread == null) {
			mThread = new Thread(this, "inputThread");
			mThread.start();
		}
	}
}

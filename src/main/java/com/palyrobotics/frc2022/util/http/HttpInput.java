package com.palyrobotics.frc2022.util.http;

import java.util.Iterator;

import org.json.JSONObject;

// tracks all the information being sent to the server
public class HttpInput {

	/*
	* When adding a new service, add a JSONObject input below
	*/

	private JSONObject output = new JSONObject();
	private JSONObject logInput = new JSONObject();
	private JSONObject chartInput = new JSONObject();
	private JSONObject configInput = new JSONObject();
	private JSONObject telemetryInput = new JSONObject();
	private JSONObject positionInput = new JSONObject();

	private HttpInput() {
		/*
		* If you need to set a default value when starting a service set here
		*/
		output.put("telemetry", telemetryInput);
		output.put("config", configInput);
	}

	public static HttpInput getInstance() {
		return sHttpInput;
	}

	private static HttpInput sHttpInput = new HttpInput();

	/*
	* Add setter for InputThread here
	*/

	public void setChartInput(JSONObject newInput) {
		chartInput = newInput;
	}

	public void setLogInput(JSONObject newInput) {
		logInput = newInput;
	}

	public void setConfigInput(JSONObject newInput) {
		configInput = newInput;
	}

	public void setTelemetry(JSONObject newInput) {
		telemetryInput = newInput;
	}

	public void setPositionInput(JSONObject newInput) {
		positionInput = newInput;
	}

	public JSONObject getInput() {

		/*
		* If the output of your service is a json object, add it as seen below
		*/

		if (!telemetryInput.isEmpty()) {
			output.put("telemetry", telemetryInput);
		}

		if (!configInput.isEmpty()) {
			output.put("config", configInput);
		}

		/*
		* If the output of your service is a json array, add it as seen below
		*/

		if (!chartInput.isEmpty()) {
			Iterator<String> chartKeys = chartInput.keys();

			while (chartKeys.hasNext()) {
				String key = chartKeys.next();
				if (key == "graphData") {
					output.put(key, chartInput.get(key));
				}
			}
		}

		if (!logInput.isEmpty()) {
			Iterator<String> logKeys = logInput.keys();

			while (logKeys.hasNext()) {
				String key = logKeys.next();
				if (key == "logs") {
					output.put(key, logInput.get(key));
				}
			}
		}

		if (!positionInput.isEmpty()) {
			Iterator<String> posKeys = positionInput.keys();

			while (posKeys.hasNext()) {
				String key = posKeys.next();
				if (key == "position") {
					output.put(key, positionInput.get(key));
				}
			}
			//output.put("position", positionInput);
		}

		return output;
	}
}

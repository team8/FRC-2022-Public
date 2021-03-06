package com.palyrobotics.frc2022.util.config;

import static java.util.Objects.isNull;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.esotericsoftware.minlog.Log;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palyrobotics.frc2022.util.control.Gains;
import com.palyrobotics.frc2022.util.control.ProfiledGains;
import com.palyrobotics.frc2022.util.http.HttpInput;

import org.json.JSONObject;

// controls how the config is written and read by the http server
public class ConfigUploadManager {

	private JSONObject config = new JSONObject();
	private static ConfigUploadManager mUploadManager = new ConfigUploadManager();

	private static ObjectMapper sMapper = Configs.getMapper();

	private ConfigUploadManager() {
	}

	public static ConfigUploadManager getInstance() {
		return mUploadManager;
	}

	public void updateConfig(JSONObject newConfig) {
		config = newConfig;
		try {
			update();
			System.out.println("UpdateConfig working...");
		} catch (Error | IllegalAccessException | NoSuchFieldException | JsonProcessingException e) {
			Log.error(e.toString());
		}
	}

	public boolean update() throws IllegalAccessException, NoSuchFieldException, JsonProcessingException {
		if (!config.isEmpty()) {
			Iterator<String> keys = config.keys();

			while (keys.hasNext()) {
				String currentKey = keys.next();
				JSONObject currentConfig = (JSONObject) config.get(currentKey);
				Class<? extends ConfigBase> configClass = Configs.getClassFromName(currentKey);
				ConfigBase configObject = Configs.get(configClass);
				Iterator<String> configKeys = currentConfig.keys();
				while (configKeys.hasNext()) {
					String temp = configKeys.next();
					Field field = getField(configClass, temp);
					try {
						if (field.getType().getName() == "com.palyrobotics.frc2022.util.control.Gains") {
							JSONObject internalJson = (JSONObject) currentConfig.get(temp);
							Gains tGains = new Gains(internalJson.getBigDecimal("p").doubleValue(), internalJson.getBigDecimal("i").doubleValue(), internalJson.getBigDecimal("d").doubleValue(), internalJson.getBigDecimal("f").doubleValue(), internalJson.getBigDecimal("iZone").doubleValue(), internalJson.getBigDecimal("iMax").doubleValue());
							Configs.set(configObject, configObject, field, tGains);
						} else if (field.getType().getName() == "com.palyrobotics.frc2022.util.control.ProfiledGains") {
							JSONObject internalJson = (JSONObject) currentConfig.get(temp);
							ProfiledGains tGains = new ProfiledGains(internalJson.getBigDecimal("p").doubleValue(), internalJson.getBigDecimal("i").doubleValue(), internalJson.getBigDecimal("d").doubleValue(), internalJson.getBigDecimal("f").doubleValue(), internalJson.getBigDecimal("iZone").doubleValue(), internalJson.getBigDecimal("iMax").doubleValue(), internalJson.getBigDecimal("acceleration").doubleValue(), internalJson.getBigDecimal("velocity").doubleValue(), internalJson.getBigDecimal("allowableError").doubleValue(), internalJson.getBigDecimal("minimumOutputVelocity").doubleValue());
							Configs.set(configObject, configObject, field, tGains);
						} else if (field.getType().getName() == "java.util.List") {
						} else if (isNull(currentConfig.get(temp))) {
							Log.debug("NULL VALUE ENCOUNTERED");
						} else {
							Configs.set(configObject, configObject, field, field.getType().getName() == "double" ? ((BigDecimal) currentConfig.get(temp)).doubleValue() : currentConfig.get(temp));
						}
					} catch (Exception e) {
					}
					Configs.save(configClass);
				}
			}

			Iterator<String> configIterator = Configs.getActiveConfigNames().iterator();
			JSONObject configJson = new JSONObject();
			Object temp;
			while (configIterator.hasNext()) {
				temp = configIterator.next();
				configJson.put(temp.toString(), new JSONObject(Configs.get(Configs.getClassFromName(temp.toString())).toString()));
			}
			HttpInput.getInstance().setConfigInput(configJson);

			return true;
		} else {
			return false;
		}
	}

	private Field getField(Class<?> clazz, String name) throws NoSuchFieldException {
		var fields = new HashMap<String, Field>();
		for (Class<?> c = clazz; c != null; c = c.getSuperclass()) {
			fields.putAll(Arrays.stream(c.getDeclaredFields())
					.collect(Collectors.toMap(Field::getName, Function.identity())));
		}
		return Optional.ofNullable(fields.get(name)).orElseThrow(NoSuchFieldException::new);
	}
}

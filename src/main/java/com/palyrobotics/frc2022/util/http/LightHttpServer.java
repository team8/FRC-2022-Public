package com.palyrobotics.frc2022.util.http;

import static com.palyrobotics.frc2022.util.Util.newWaypoint;
import static com.palyrobotics.frc2022.util.http.HttpInput.getInstance;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;

import kotlin.Pair;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.RoutineManager;
import com.palyrobotics.frc2022.behavior.SequentialRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ShootCustomRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.util.RoutinesList;
import com.palyrobotics.frc2022.util.config.ConfigUploadManager;
import com.palyrobotics.frc2022.util.service.LogEntry;
import com.palyrobotics.frc2022.vision.Limelight;

import io.javalin.Javalin;
import io.javalin.core.JavalinConfig;

import org.json.JSONArray;
import org.json.JSONObject;

// backend server through which all control center data is sent and received
public class LightHttpServer implements Runnable {

	private JSONObject lastInput = new JSONObject();
	private boolean connected = false;
	private int port;
	private Javalin app;
	private Commands commands;
	private RobotState state;
	private RoutineManager routineManager;
	private Limelight limelight = Limelight.getInstance();
	private double wantedHoodAngle;
	private double wantedUpperFlyWheelVelocity;
	private double wantedLowerFlyWheelVelocity;
	private JSONArray logs = new JSONArray();
	private JSONObject graph = new JSONObject();

	@Override
	public void run() {
	}

	public void setServer(int newPort) {
		// set up server here
		Thread serverThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					app = Javalin.create(JavalinConfig::enableCorsForAllOrigins).start(newPort);
					app.get("/", ctx -> ctx.result("Server Running"));
					setupEndpoints();
					connected = true;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		serverThread.setPriority(Thread.MIN_PRIORITY);
		serverThread.start();
	}

	public LightHttpServer(Commands commands, RobotState state, RoutineManager manager) {
		this.commands = commands;
		this.state = state;
		this.routineManager = manager;
	}

	private static ArrayList<Pair<String, String>> getConstructorMap(Constructor method) {
		Parameter[] parameters = method.getParameters();
		ArrayList<Pair<String, String>> parameterNames = new ArrayList<>();

		for (Parameter parameter : parameters) {
			if (!parameter.isNamePresent()) {
				throw new IllegalArgumentException("Parameter names are not present!");
			}

			String parameterName = parameter.getName();
			String parameterType = parameter.getType().toString();
			parameterNames.add(new Pair<>(parameterName, parameterType));
		}

		return parameterNames;
	}

	public void addLog(LogEntry log) {
		if (logs.length() >= 50) {
			logs.remove(0);
		}
		logs.put(log.toString());
	}

	public void addGraphPoints(JSONObject newPoints) {
		graph = newPoints;
	}

	public void setupEndpoints() {
		/*
		* add new endpoint for service
		*/

		app.get("/config", ctx -> {
			JSONObject tempJson = getInstance().getInput().getJSONObject("config");
			ctx.result(tempJson.toString());
		});

		app.post("/config", ctx -> {
			JSONObject tempJson = new JSONObject(ctx.body());
			ConfigUploadManager.getInstance().updateConfig(tempJson);
			ctx.result(tempJson.toString());
		});

		app.get("/graphs", ctx -> {
			ctx.result(graph.toString());
		});

		app.get("/logs", ctx -> {
			ctx.result(logs.toString());
		});

		app.get("/telemetry", ctx -> {
			JSONObject tempJson = getInstance().getInput().getJSONObject("telemetry");
			ctx.result(tempJson.toString());
		});

		app.get("/position", ctx -> {
			JSONArray tempJson = getInstance().getInput().getJSONArray("position");
			ctx.result(tempJson.toString());
		});

		// Gets the distance from the target. Returns a double in inches
		app.get("/shooter/distance", ctx -> {
			ctx.result(String.valueOf(limelight.getEstimatedDistanceInches()));
		});

		/* Change what angle the hood will move to
		{
			"velocity": x // velocity to spin the lower flywheel at. WILL make the shooter hood move.
		}
		*/
		app.post("/shooter/hood", ctx -> {
			JSONObject tmpJson = new JSONObject(ctx.body());
			wantedHoodAngle = tmpJson.getDouble("angle");
			commands.shooterWantedAngle = wantedHoodAngle;
			commands.shooterWantedState = Shooter.State.AIMING;
		});

		app.get("/shooter/velocities", ctx -> {
			JSONObject tmp = new JSONObject();
			tmp.put("upper", state.shooterUpperVelocity);
			tmp.put("lower", state.shooterLowerVelocity);
			tmp.put("hood", state.shooterHoodPosition);
			ctx.result(tmp.toString());
		});

		/* Change what velocity the upper flywheel will spin at
		{
			"velocity": x // velocity to spin the lower flywheel at
		}
		*/
		app.post("/shooter/upper", ctx -> {
			JSONObject tmpJson = new JSONObject(ctx.body());
			wantedUpperFlyWheelVelocity = tmpJson.getDouble("velocity");
			commands.shooterWantedVelocityUpper = wantedUpperFlyWheelVelocity;
			commands.shooterWantedState = Shooter.State.CUSTOM;
			if (wantedUpperFlyWheelVelocity == 0) {
				commands.setShooterIdle();
			}
		});

		/* Change what velocity the lower flywheel will spin at
		{
			"velocity": x // velocity to spin the lower flywheel at
		}
		*/
		app.post("/shooter/lower", ctx -> {
			JSONObject tmpJson = new JSONObject(ctx.body());
			wantedLowerFlyWheelVelocity = tmpJson.getDouble("velocity");
			commands.shooterWantedVelocityLower = wantedLowerFlyWheelVelocity;
			commands.shooterWantedState = Shooter.State.CUSTOM;
			if (wantedLowerFlyWheelVelocity == 0) {
				commands.setShooterIdle();
			}
		});

		/* Moves x inches forward or backward
		{
			"distance": x // distance to move, can be negative
		}
		*/
		app.post("/shooter/move", ctx -> {
			JSONObject tmp = new JSONObject(ctx.body());
			var wantedDistance = tmp.getDouble("distance");
			commands.addWantedRoutine(new SequentialRoutine(
					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
					new DrivePathRoutine(newWaypoint(wantedDistance, 0.0, 0.0))));
		});

		// Shoot a ball with the angle, upper, and lower values set before. If not set they will default to 0
		app.post("/shooter/shoot", ctx -> {
			commands.addWantedRoutine(new ShootCustomRoutine(5, wantedHoodAngle, wantedLowerFlyWheelVelocity, wantedUpperFlyWheelVelocity));
		});
		// Takes a json object and adds that routine to the wanted routines list
		/* json object should look like:
		{
			"classpath": "com.palyrobotics...",
			"args": [ 1, 2 ]
		}
		*/
		app.post("/routines/start", ctx -> {
			try {
				JSONObject tmpJson = new JSONObject(ctx.body());
				var routine = tmpJson.getString("classpath");
				var argsArray = tmpJson.getJSONArray("args");
				var args = new ArrayList<>();
				for (var arg : argsArray) {
					if (arg.getClass().equals(BigDecimal.class)) {
						// sketchy issue = sketch fix
						args.add(((BigDecimal) arg).doubleValue());
					} else {
						args.add(arg);
					}
				}
				var clazz = Class.forName(routine);
				var constructors = clazz.getConstructors();
				Arrays.sort(constructors, Comparator.comparingInt(Constructor::getParameterCount));
				var constructor = constructors[0];
				commands.addWantedRoutine((RoutineBase) constructor.newInstance(args.toArray()));
				ctx.result("Routine start successful.");
				// TODO: make sure that the args are in the correct order
			} catch (Exception e) {
				ctx.result(String.format("Routine start failed. Exception:\n%s", e));
			}
		});

		// Gets all existing routines. Returns a json list of java classpath
		app.get("/routines/all", ctx -> {
			// TODO: hardcode this
			JSONObject tmpJson = new JSONObject();
			RoutinesList.routines.forEach(clazz -> {
				var params = clazz.getConstructors();
				Arrays.sort(params, Comparator.comparingInt(Constructor::getParameterCount));

				var map = getConstructorMap(params[0]);
				tmpJson.put(clazz.toString(), map);
			});
			ctx.result(tmpJson.toString());
		});

		// Gets all currently running routines. Returns a json list of java classpath
		app.get("/routines/running", ctx -> {
			JSONObject tmpJson = new JSONObject();
			var running = routineManager.getCurrentRoutines();
			running.stream().forEach(i -> {
				var clazz = i.getClass();
				var params = clazz.getConstructors();
				Arrays.sort(params, Comparator.comparingInt(Constructor::getParameterCount));

				var map = getConstructorMap(params[0]);
				tmpJson.put(clazz.toString(), map);
			});
			ctx.result(tmpJson.toString());
		});

		// Takes a string which is a java classpath and removes that routine from the running ones
		app.post("/routines/stop", ctx -> {
			var routine = ctx.body().toString();
			commands.stopRoutine((Class<? extends RoutineBase>) Class.forName(routine));
		});
	}

	public boolean getConnected() {
		return connected;
	}
}

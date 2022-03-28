package com.palyrobotics.frc2022.util;

import java.lang.reflect.Modifier;
import java.util.Set;
import java.util.stream.Collectors;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.behavior.routines.*;

import org.reflections.Reflections;

public class RoutinesList {

	public static Set<Class<? extends RoutineBase>> routines;

	static {
		Reflections reflections = new Reflections("com.palyrobotics");

		routines = reflections.getSubTypesOf(RoutineBase.class)
				.stream()
				.filter(i -> !Modifier.isAbstract(i.getModifiers()))
				.collect(Collectors.toSet());
	}
}

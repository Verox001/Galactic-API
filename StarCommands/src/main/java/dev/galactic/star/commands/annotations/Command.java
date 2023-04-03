/*
 * Copyright 2023 Galactic Star Studios
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.galactic.star.commands.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The main command annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
	/**
	 * The name of the command.
	 *
	 * @return Command name.
	 */
	String value();

	/**
	 * The description of the command.
	 *
	 * @return Command description.
	 */
	String desc() default "";

	/**
	 * The message that is sent when a player executes the command wrong.
	 *
	 * @return Usage message.
	 */
	String usage();

	/**
	 * Whether to only allow the console to execute it. Defaults to false.
	 *
	 * @return True or false.
	 */
	boolean consoleOnly() default false;

	/**
	 * Whether to only allow the player to execute it. Defaults to false.
	 *
	 * @return True or false.
	 */
	boolean playerOnly() default false;

	/**
	 * The alias you can use with the command.
	 *
	 * @return Array of aliases.
	 */
	String[] aliases() default {};
}

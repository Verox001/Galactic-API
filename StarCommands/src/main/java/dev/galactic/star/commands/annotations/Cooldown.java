/*
 * Copyright 2023 Galactic Star Studios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.concurrent.TimeUnit;

/**
 * The annotation that declares a cool down between entering commands.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cooldown {
	/**
	 * The amount of time of the unit specified.
	 *
	 * @return Long time in the units.
	 */
	long time();

	/**
	 * The timeunit in which you have to wait before you can execute the command.
	 *
	 * @return TimeUnit.
	 * @see TimeUnit
	 */
	TimeUnit unit();

	/**
	 * Whether the console should have the cooldown too.
	 *
	 * @return True or false.
	 */
	boolean consoleToo() default false;

	/**
	 * The permission that bypasses the command.
	 *
	 * @return Permission.
	 */
	String bypassPerm() default "";
}

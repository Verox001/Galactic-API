
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

package dev.galactic.star.commands;

import dev.galactic.star.commands.annotations.*;
import dev.galactic.star.commands.exceptions.AnnotationNotFoundException;
import dev.galactic.star.commands.exceptions.DuplicateDefaultAnnotationException;
import dev.galactic.star.commands.exceptions.DuplicateOptionalArgsAnnotationException;
import dev.galactic.star.commands.exceptions.IllegalOptionalArgsAnnotationException;
import dev.galactic.star.commands.managers.AbstractCommand;
import dev.galactic.star.commands.managers.CooldownManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginBase;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Register {
	/**
	 * Custom tab completing options.
	 */
	public static final HashMap<String, List<String>> customOptions = new HashMap<>();
	/**
	 * The config where the list of options can be.
	 */
	public static FileConfiguration config;
	private static SimpleCommandMap commandMap;
	private final PluginBase plugin;
	private final CooldownManager manager = new CooldownManager();
	private SimplePluginManager pluginManager;

	public Register(PluginBase examplePlugin) {
		customOptions.clear();
		this.plugin = examplePlugin;
		this.setCommandMap();
	}

	/**
	 * Sends the no permission message if they don't have the permission required.
	 *
	 * @param sender     CommandSender instance.
	 * @param permission Permission annotation.
	 * @return True if they don't have the permission else false.
	 */
	public static boolean sendNoPermMessage(CommandSender sender, Permission permission) {
		if (!sender.hasPermission(permission.value())) {
			sender.sendMessage(ChatColor.RED + permission.noPermMsg());
			return true;
		}
		return false;
	}

	/**
	 * Checks whether the sender has the permission required.
	 *
	 * @param sender     CommandSender instance.
	 * @param permission Permission annotation.
	 * @return True if they do, else false.
	 */
	public static boolean hasPermission(CommandSender sender, Permission permission) {
		return sender.hasPermission(permission.value());
	}

	/**
	 * Resets the config.
	 */
	public void reloadConfig() {
		config = this.plugin.getConfig();
	}

	/**
	 * Register some custom tab completions.
	 *
	 * @param id             ID of the tab completion.
	 * @param tabCompletions List&lt;String&gt; of the options.
	 */
	public void registerCustomOptions(String id, List<String> tabCompletions) {
		customOptions.put(id, tabCompletions);
	}

	/**
	 * Register Annotation commands.
	 *
	 * @param unregister Whether to unregister the non-custom commands with the same name as the custom ones.
	 * @param objects    Array of the Command classes.
	 */
	public void register(boolean unregister, Object... objects) {
		config = this.plugin.getConfig();
		setCommandMap();
		for (Object o : objects) {
			Class<?> c = o.getClass();
			Command cmd = this.getCommand(c);
			try {
				if (c.getDeclaredAnnotationsByType(Default.class).length > 1) {
					throw new DuplicateDefaultAnnotationException();
				} else if (Arrays.stream(c.getMethods())
						.anyMatch(e -> e.getDeclaredAnnotationsByType(OptionalArgs.class).length > 1)) {
					throw new IllegalOptionalArgsAnnotationException(
							"You can only have one @OptionalArgs annotation in a method at a time.");

				}
			} catch (IllegalOptionalArgsAnnotationException | DuplicateDefaultAnnotationException e) {
				throw new RuntimeException(e);
			}
			if (unregister) {
				org.bukkit.command.Command command = commandMap.getCommand(cmd.value());
				if (command != null) {
					command.unregister(commandMap);
				}
			}
			org.bukkit.command.Command abstractCommand = new AbstractCommand(this.manager, this, cmd,
					this.getClassPermission(c), o
			);
			commandMap.register(cmd.value(), abstractCommand);
		}
	}

	/**
	 * Getter for the cooldown manager.
	 *
	 * @return CooldownMangaer instance.
	 * @see CooldownManager
	 */
	public CooldownManager getCooldowmManager() {
		return this.manager;
	}

	private void setCommandMap() {
		pluginManager = (SimplePluginManager) this.plugin.getServer().getPluginManager();
		Field f = null;
		try {
			f = SimplePluginManager.class.getDeclaredField("commandMap");
		} catch (Exception e) {
			e.printStackTrace();
		}
		f.setAccessible(true);
		try {
			commandMap = (SimpleCommandMap) f.get(pluginManager);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Checks whether there are subcommands.
	 *
	 * @param c Class&lt;?&gt;
	 * @return True if there are, false else.
	 */
	public boolean subCommandsExist(Class<?> c) {
		return this.getSubCommands(c).size() > 0;
	}

	/**
	 * Returns the command base permission.
	 *
	 * @param c Class&lt;/?&gt;
	 * @return Permission annotation instance.
	 * @see Permission
	 */
	public Permission getClassPermission(Class<?> c) {
		return c.getAnnotation(Permission.class);
	}

	/**
	 * Returns the @Command annotation.
	 *
	 * @param c Class&lt;/?&gt;
	 * @return Command annotation instance.
	 * @see Command
	 */
	public Command getCommand(Class<?> c) {
		Command cmd = c.getAnnotation(Command.class);
		if (cmd == null) {
			try {
				throw new AnnotationNotFoundException("@Command not found in the class specified.");
			} catch (AnnotationNotFoundException e) {
				e.printStackTrace();
			}
		}
		return cmd;
	}

	/**
	 * Returns the parameters of the method.
	 *
	 * @param method          Method to get the parameters of.
	 * @param includeOptional Whether to include the @OptionArgs at the end of the list.
	 * @return List&lt;Parameter&gt;
	 * @see Parameter
	 */
	public int getParameterSize(Method method, boolean includeOptional) {
		Parameter[] params = method.getParameters();
		if (this.hasOptionalArgs(method)) {
			if (includeOptional) return params.length - 1;
			return params.length - 2;
		}
		return params.length - 1;
	}

	/**
	 * Checks whether it has an @OptionalArgs annotation.
	 *
	 * @param method Method to check.
	 * @return True if it does else false
	 */
	public boolean hasOptionalArgs(Method method) {
		Parameter[] params = method.getParameters();
		if (Arrays.stream(params).filter(e -> e.isAnnotationPresent(OptionalArgs.class)).count() > 1) {
			try {
				throw new DuplicateOptionalArgsAnnotationException("There can only be one @OptionArgs annotation at " +
						"the end!");
			} catch (DuplicateOptionalArgsAnnotationException e) {
				throw new RuntimeException(e);
			}
		}
		return params[params.length - 1].isAnnotationPresent(OptionalArgs.class);
	}

	/**
	 * Returns @TabCompletion annotation.
	 *
	 * @param method Method to get from.
	 * @return TabCompletion annotation instance.
	 */
	public TabCompletion getTabCompletion(Method method) {
		return method.getDeclaredAnnotation(TabCompletion.class);
	}

	/**
	 * Gets the method handler by the subcommand name.
	 *
	 * @param c    Class&lt;?&gt;
	 * @param name SubCommand name.
	 * @return Method handler. Null if doesn't exist.
	 */
	public Method getMethodBySubName(Class<?> c, String name) {
		Optional<Method> optional = this.getSubCommandMethods(c)
				.stream()
				.filter(e -> e.getDeclaredAnnotation(SubCommand.class).value().equals(name))
				.findFirst();
		return optional.orElse(null);
	}

	/**
	 * Returns the Permission attached to the certain subcommand.
	 *
	 * @param c          Class&lt;?&gt;
	 * @param subCommand SubCommand name.
	 * @return Permission annotation.
	 */
	public Permission getSubCommandPermByName(Class<?> c, String subCommand) {
		Method method = this.getMethodBySubName(c, subCommand);
		return method == null ? null : method.getDeclaredAnnotation(Permission.class);
	}

	/**
	 * Returns the permission of the @Default method.
	 *
	 * @param c Class to check.
	 * @return Permission annotation.
	 */
	public Permission getDefaultHanlderPermission(Class<?> c) {
		Method method = this.getDefaultHandler(c);
		return method == null ? null : method.getDeclaredAnnotation(Permission.class);
	}

	/**
	 * Returns the @Default annotation.
	 *
	 * @param c Class to check.
	 * @return Default annotation.
	 */
	public Default getDefaultAnnotation(Class<?> c) {
		return this.getDefaultHandler(c)
				.getDeclaredAnnotation(Default.class);
	}

	/**
	 * Returns the @SubCommand annotation by its name.
	 *
	 * @param c    Class&lt;?&gt;
	 * @param name Name of the subcommand.
	 * @return SubCommand annotation.
	 */
	public SubCommand getSubCommandByName(Class<?> c, String name) {
		Optional<SubCommand> optional = this.getSubCommands(c)
				.stream()
				.filter(e -> e.value().equals(name))
				.findAny();
		return optional.orElse(null);
	}

	/**
	 * Returns the method that is annotated with @Default.
	 *
	 * @param c Class&lt;/?&gt;
	 * @return Default annotation instance.
	 * @see Default
	 */
	public Method getDefaultHandler(Class<?> c) {
		return Arrays.stream(c.getDeclaredMethods())
				.filter(e -> e.isAnnotationPresent(Default.class))
				.findFirst()
				.orElse(null);
	}

	/**
	 * Returns a List of @SubCommand annotations.
	 *
	 * @param c Class&lt;?&gt;
	 * @return List&lt;SubCommand&gt;
	 * @see SubCommand
	 */
	public List<SubCommand> getSubCommands(Class<?> c) {
		return this.getSubCommandMethods(c)
				.stream()
				.map(e -> e.getAnnotation(SubCommand.class))
				.collect(Collectors.toList());
	}

	/**
	 * Returns a List of Methods that have the @SubCommand annotation.
	 *
	 * @param c Class&lt;?&gt;
	 * @return List&lt;Method&gt;
	 * @see Method
	 */
	public List<Method> getSubCommandMethods(Class<?> c) {
		return Arrays.stream(c.getDeclaredMethods())
				.filter(e -> e.isAnnotationPresent(SubCommand.class))
				.collect(Collectors.toList());
	}

	public List<String> getSubCommandNames(Class<?> c) {
		return this.getSubCommands(c)
				.stream()
				.map(SubCommand::value)
				.collect(Collectors.toList());
	}

	/**
	 * Getter for the plugin instance.
	 *
	 * @return PluginBase instance.
	 * @see PluginBase
	 */
	public PluginBase getPlugin() {
		return this.plugin;
	}
}

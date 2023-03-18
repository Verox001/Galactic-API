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

package dev.galactic.star.commands.managers;

import dev.galactic.star.commands.Register;
import dev.galactic.star.commands.annotations.Command;
import dev.galactic.star.commands.annotations.Permission;
import dev.galactic.star.commands.annotations.SubCommand;
import dev.galactic.star.commands.annotations.TabCompletion;
import dev.galactic.star.commands.exceptions.DefaultAnnotationNotFoundException;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractCommand extends org.bukkit.command.Command implements PluginIdentifiableCommand {
	private final Register register;
	private final Command cmd;
	private final Permission permission;
	private final Object o;
	private final Class<?> clazz;

	public AbstractCommand(Register register, Command cmd, Permission permission, Object o) {
		super(cmd.value(), cmd.desc(), cmd.usage(), Arrays.asList(cmd.aliases()));
		if (permission != null) {
			this.setPermission(permission.value());
			this.setPermissionMessage(permission.noPermMsg());
		}
		this.register = register;
		this.cmd = cmd;
		this.permission = permission;
		this.o = o;
		this.clazz = o.getClass();
	}

	@Override
	public Plugin getPlugin() {
		return this.register.getPlugin();
	}

	@Override
	public boolean execute(CommandSender sender, String alias, String[] args) {
		if (Register.sendNoPermMessage(sender, this.permission)) return false;

		try {
			if (this.cmd.playerOnly() && sender instanceof ConsoleCommandSender) {
				sender.sendMessage(ChatColor.RED + "Sorry, but you have to be a player to use this command.");
				return false;
			} else if (this.cmd.consoleOnly() && sender instanceof Player) {
				sender.sendMessage(ChatColor.RED + "Sorry, but you have to be the console to use this command.");
				return false;
			} else if (args.length == 0) {
				if (this.register.getSubCommands(this.clazz).size() > 0) {
					sender.sendMessage(ChatColor.RED + this.cmd.usage());
					return false;
				} else if (this.register.getSubCommands(this.clazz).size() == 0 && this.register.getDefaultHandler(this.clazz) == null) {
					try {
						throw new DefaultAnnotationNotFoundException();
					} catch (DefaultAnnotationNotFoundException e) {
						e.printStackTrace();
					}
					return false;
				}
				Method method = this.register.getDefaultHandler(this.clazz);
				method.invoke(this.o, sender);
				return true;
			}
			Method method = this.register.getMethodBySubName(this.clazz, args[0]);

			if (method == null) {
				sender.sendMessage(ChatColor.RED + this.cmd.usage());
				return false;
			}
			TabCompletion completion = this.register.getTabCompletion(method);
			if (!this.canExecute(sender, args, completion)) {
				return false;
			}
			Object[] argArray;
			if (this.register.hasOptionalArgs(method)) {
				argArray = new Object[this.register.getParameterSize(method, true) + 1];
				argArray[argArray.length - 1] = null;
			} else {
				argArray = new Object[this.register.getParameterSize(method, true) + 2];
			}
			for (int i = 0; i < argArray.length; i++) {
				if (this.register.hasOptionalArgs(method)) {
					if (i == args.length - 1) break;
				} else if (i == argArray.length - 1) {
					break;
				}
				if (!CompletionId.isValid(sender, completion, i, args[i + 1])) {
					return false;
				}
				argArray[i + 1] = CompletionId.getObjArgsFrom(completion, i, args[i + 1]);
			}
			argArray[0] = sender;
			System.out.println(argArray.length);
			System.out.println(Arrays.toString(argArray));
			method.invoke(this.o, argArray);
			return true;
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean canExecute(CommandSender sender, String[] args, TabCompletion completion) {
		Method method = this.register.getMethodBySubName(this.clazz, args[0]);

		SubCommand subCommand = this.register.getSubCommandByName(this.clazz, args[0]);
		Permission perm = this.register.getSubCommandPermByName(this.clazz, args[0]);

		if (perm != null && Register.sendNoPermMessage(sender, perm)) {
			return false;
		} else if (!this.hasExactValues(method, args, completion)) {
			sender.sendMessage(ChatColor.RED + subCommand.usage());
			return false;
		}
		return true;
	}

	private boolean canTabCompleteValues(CommandSender sender, String[] args, boolean isCompletingCommands) {
		if (!this.register.getSubCommandNames(this.clazz).contains(args[0])) {
			return false;
		}
		Permission perm = this.register.getSubCommandPermByName(this.clazz, args[0]);
		if (isCompletingCommands) {
			if (perm == null) return true;
			return Register.hasPermission(sender, perm);
		}
		TabCompletion tabComplete = this.register.getTabCompletion(this.register.getMethodBySubName(
				this.clazz,
				args[0]
		));
		if ((perm != null && !Register.hasPermission(sender, perm))) {
			return false;
		} else if (tabComplete == null) {
			return false;
		}
		return tabComplete.value().length >= args.length - 1;
	}

	private boolean hasExactValues(Method method, String[] args, TabCompletion completion) {
		String[] completionVal = completion.value();
		int argLen = args.length - 1;
		int completeValLen = completionVal.length;
		int paramSizeNoOpt = this.register.getParameterSize(method, false) + 1;
		System.out.println("PARAM SIZE: " + paramSizeNoOpt);
		System.out.println("ARG LEN: " + argLen);
		if (this.register.hasOptionalArgs(method)) {
			if (argLen > paramSizeNoOpt) {
				return false;
			} else if (argLen == paramSizeNoOpt) {
				return true;
			}
			return argLen == paramSizeNoOpt - 1;
		} else {
			if (argLen == paramSizeNoOpt) {
				return true;
			} else if (argLen > paramSizeNoOpt - 1) {
				return false;
			}
			return false;
		}
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		if (!Register.hasPermission(sender, this.permission)) return new ArrayList<>();
		if (this.register.getSubCommands(this.clazz).size() > 0 && !args[0].matches("\\s")) {
			if (args.length == 1) {
				return this.register.getSubCommands(this.clazz).stream().map(SubCommand::value).filter(value -> this.canTabCompleteValues(sender, new String[]{value}, true)).collect(Collectors.toList());
			}

			String subCommand = args[0];

			if (!this.canTabCompleteValues(sender, args, false)) {
				return new ArrayList<>();
			}

			return CompletionId.getArgsFromObj(this.register.getTabCompletion(this.register.getMethodBySubName(this.clazz, subCommand)), args.length - 2);
		}
		return new ArrayList<>();
	}
}

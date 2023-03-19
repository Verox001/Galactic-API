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
	private final CooldownManager manager;

	public AbstractCommand(CooldownManager manager, Register register, Command cmd, Permission permission, Object o) {
		super(cmd.value(), cmd.desc(), cmd.usage(), Arrays.asList(cmd.aliases()));
		if (permission != null) {
			this.setPermission(permission.value());
			this.setPermissionMessage(permission.noPermMsg());
		}
		this.manager = manager;
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
		if (this.permission != null && Register.sendNoPermMessage(sender, this.permission)) return false;
		Method method = this.register.getDefaultHandler(this.clazz);
		try {
			if (this.manager.isInCoolDown(sender)) {
				return false;
			}
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
				} else if (this.register.getSubCommands(this.clazz).size() == 0 && !this.isDefault()) {
					try {
						throw new DefaultAnnotationNotFoundException();
					} catch (DefaultAnnotationNotFoundException e) {
						e.printStackTrace();
					}
					return false;
				}
				method.invoke(this.o, sender);
				return true;
			}
			if (method == null) {
				method = this.register.getMethodBySubName(this.clazz, args[0]);
			}
			TabCompletion completion = this.register.getTabCompletion(method);
			if (!this.canExecute(sender, args, completion)) {
				return false;
			}
			Object[] argArray = new Object[this.register.getParameterSize(method, true) + 1];
			if (this.register.hasOptionalArgs(method)) {
				argArray[argArray.length - 1] = null;
			}
			argArray[0] = sender;
			TabCompletion tabCompletion = this.register.getTabCompletion(method);
			for (int i = 0; i < argArray.length; i++) {
				if (i == argArray.length - 1 || (i == argArray.length - 1 && this.register.hasOptionalArgs(method))) {
					break;
				}
				if (this.isDefault()) {
					if (i == args.length) {
						break;
					}
					argArray[i + 1] = CompletionId.getObjArgsFrom(tabCompletion, i, args[i]);
				} else {
					if (i + 1 == args.length) {
						break;
					}
					argArray[i + 1] = CompletionId.getObjArgsFrom(tabCompletion, i, args[i + 1]);
				}
			}
			method.invoke(this.o, argArray);
			this.manager.add(sender, this.register.getCooldowmManager().getCooldown(this.clazz));
			return true;
		} catch (InvocationTargetException | IllegalAccessException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean canExecute(CommandSender sender, String[] args, TabCompletion completion) {
		Method method = this.register.getDefaultHandler(this.clazz);
		if (!this.isDefault()) {
			method = this.register.getMethodBySubName(this.clazz, args[0]);
		}
		SubCommand subCommand = this.register.getSubCommandByName(this.clazz, args[0]);
		Permission perm = this.register.getSubCommandPermByName(this.clazz, args[0]);

		if (perm != null && Register.sendNoPermMessage(sender, perm)) {
			return false;
		} else if (!this.hasExactValues(method, args, completion)) {
			String message = subCommand == null ? this.cmd.usage() : subCommand.usage();
			sender.sendMessage(ChatColor.RED + message);
			return false;
		}
		return true;
	}

	private boolean canTabCompleteValues(CommandSender sender, String[] args, boolean isCompletingCommands) {
		Permission perm = isDefault() ? this.register.getDefaultHanlderPermission(this.clazz) :
				this.register.getSubCommandPermByName(this.clazz, args[0]);
		if (isCompletingCommands) {
			if (perm == null) return true;
			return Register.hasPermission(sender, perm);
		}
		if (this.isDefault()) {
			TabCompletion tabComplete = this.register.getTabCompletion(this.register.getDefaultHandler(this.clazz));
			if (tabComplete == null) return false;
			return tabComplete.value().length >= args.length - 1;
		} else {
			if (!this.register.getSubCommandNames(this.clazz).contains(args[0])) {
				return false;
			}
			TabCompletion tabComplete = this.register.getTabCompletion(this.register.getMethodBySubName(
					this.clazz,
					args[0]
			));
			if (tabComplete == null) return false;
			return tabComplete.value().length >= args.length - 1;
		}
	}

	private boolean isDefault() {
		return this.register.getDefaultHandler(this.clazz) != null;
	}

	private boolean hasExactValues(Method method, String[] args, TabCompletion completion) {
		int argLen = args.length - 1;
		int paramSizeNoOpt = this.register.getParameterSize(method, true);
		if (this.isDefault()) {
			argLen += 1;
		}
		if (this.register.hasOptionalArgs(method)) {
			if (argLen == paramSizeNoOpt - 1 || argLen == paramSizeNoOpt) {
				return true;
			} else if (argLen > paramSizeNoOpt || argLen < paramSizeNoOpt - 1) {
				return false;
			}
		} else {
			return argLen <= paramSizeNoOpt && argLen >= paramSizeNoOpt;
		}
		return false;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
		if (this.permission != null && !Register.hasPermission(sender, this.permission)) return new ArrayList<>();
		if (this.register.getSubCommands(this.clazz).size() > 0 && !args[0].matches("\\s")) {
			if (args.length == 1) {
				return this.register.getSubCommands(this.clazz).stream().map(SubCommand::value).filter(value -> this.canTabCompleteValues(sender, new String[]{value}, true)).collect(Collectors.toList());
			}

			String subCommand = args[0];

			if (!this.canTabCompleteValues(sender, args, false)) {
				return new ArrayList<>();
			}

			return CompletionId.getArgsFromObj(this.register.getTabCompletion(this.register.getMethodBySubName(this.clazz, subCommand)), args.length - 2);
		} else if (this.register.getSubCommands(this.clazz).size() == 0 && this.isDefault()) {
			if (!this.canTabCompleteValues(sender, args, true)) {
				return new ArrayList<>();
			}
			TabCompletion completion = this.register.getTabCompletion(this.register.getDefaultHandler(this.clazz));
			if (completion == null || args.length > completion.value().length) return new ArrayList<>();
			return CompletionId.getArgsFromObj(completion, args.length - 1);
		}
		return new ArrayList<>();
	}
}

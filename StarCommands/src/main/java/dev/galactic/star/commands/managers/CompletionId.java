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

package dev.galactic.star.commands.managers;

import dev.galactic.star.commands.Register;
import dev.galactic.star.commands.annotations.TabCompletion;
import dev.galactic.star.commands.exceptions.UnknownCompletionIdException;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum CompletionId {
	/**
	 * The Player id.
	 */
	PLAYER("\\.player"),

	/**
	 * The Range id.
	 */
	RANGE("\\.range\\(\\d+-\\d+\\)"),

	/**
	 * The Material id.
	 */
	MATERIAL("\\.material"),

	/**
	 * The Boolean id.
	 */
	BOOLEAN("\\.boolean"),

	/**
	 * The Sound id.
	 */
	SOUND(".sound"),

	/**
	 * The World id.
	 */
	WORLD("\\.world"),

	/**
	 * The Entity id.
	 */
	ENTITY("\\.entity"),

	/**
	 * Gets the list from the config.yml ONLY FOR NOW.
	 */
	CONFIG("\\.config\\([\\w\\.]+\\)"),

	/**
	 * The Empty id.
	 */
	EMPTY("\\.empty");

	private final String id;

	CompletionId(String id) {
		this.id = id;
	}

	/**
	 * Returns a HashMap of the type and options.
	 *
	 * @param completion @TabCompletion annotation.
	 * @return HashMap&lt;String, List&lt;String&gt;&gt;
	 */
	public static List<String> getArgsFromObj(TabCompletion completion, int index) {
		if (completion == null) {
			return new ArrayList<>();
		}
		String[] values = completion.value();

		if (values.length == 0) {
			throw new IllegalArgumentException("Please specify a type of tab completion. It can't be blank.");
		}
		String val = values[index];
		if (val.matches(PLAYER.id)) {
			return Bukkit.getOnlinePlayers()
					.stream()
					.map(Player::getName)
					.sorted()
					.collect(Collectors.toList());
		} else if (val.matches(RANGE.id)) {
			String[] splitLowerCase = val.replaceAll("(\\.range|[\\(\\)])", "")
					.split("-");
			int fromRange = Integer.parseInt(splitLowerCase[0]);
			int toRange = Integer.parseInt(splitLowerCase[1]);
			List<String> lis = new ArrayList<>();
			for (int i = fromRange; i < toRange; i++) {
				lis.add(String.valueOf(i));
			}
			return lis;
		} else if (val.matches(MATERIAL.id)) {
			return Arrays.stream(Material.values())
					.map(Enum::name)
					.sorted()
					.collect(Collectors.toList());
		} else if (val.matches(BOOLEAN.id)) {
			return Arrays.asList("true", "false");
		} else if (val.matches(SOUND.id)) {
			return Arrays.stream(Sound.values())
					.map(Enum::name)
					.sorted()
					.collect(Collectors.toList());
		} else if (val.matches(WORLD.id)) {
			return Bukkit.getWorlds()
					.stream()
					.map(World::getName)
					.sorted()
					.collect(Collectors.toList());
		} else if (val.matches(ENTITY.id)) {
			return Arrays.stream(EntityType.values())
					.map(EntityType::name)
					.sorted()
					.collect(Collectors.toList());
		} else if (val.matches(EMPTY.id)) {
			return new ArrayList<>();
		} else if (val.matches(CONFIG.id)) {
			String configPath = val.replaceAll("(\\.config|[\\(\\)])", "");
			System.out.println(configPath);
			FileConfiguration config = Register.config;
			if (config == null) {
				System.out.println("NO CONFIG");
				return new ArrayList<>();
			}
			return config.getStringList(configPath);
		}
		if (Register.customOptions.containsKey(val)) {
			return Register.customOptions.get(val);
		}
		try {
			throw new UnknownCompletionIdException("Illegal @TabCompletion ID \"" + val + "\"");
		} catch (UnknownCompletionIdException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the Object of the specified option.
	 *
	 * @param completion @TabCompletion annotation.
	 * @param index      Index of the type of tab complete.
	 * @param option     Option the user chose.
	 * @return Object of option type.
	 */
	public static Object getObjArgsFrom(TabCompletion completion, int index, String option) {
		String[] values = completion.value();
		if (values.length == 0) {
			throw new IllegalArgumentException("Please specify a type of tab completion. It can't be blank.");
		}
		if (completion.value().length <= index) {
			return option;
		}
		String val = values[index];
		if (val.matches(PLAYER.id)) {
			return Bukkit.getPlayer(option);
		} else if (val.matches(RANGE.id)) {
			return Integer.valueOf(option);
		} else if (val.matches(MATERIAL.id)) {
			return Material.getMaterial(option);
		} else if (val.equals(BOOLEAN.id)) {
			return Boolean.getBoolean(option);
		} else if (val.equals(SOUND.id)) {
			return Sound.valueOf(option.toUpperCase());
		} else if (val.equals(WORLD.id)) {
			return Bukkit.getWorld(option);
		} else if (val.equals(ENTITY.id)) {
			return EntityType.valueOf(option.toUpperCase());
		}
		return option;
	}

	/**
	 * Checks whether the options are a valid tab complete option.
	 *
	 * @param sender     CommandSender.
	 * @param completion @TabCompletion annotation.
	 * @param index      Index of the type of tab complete.
	 * @param option     Option the sender chose.
	 * @return True if valid, false if not.
	 */
	public static boolean isValid(CommandSender sender, TabCompletion completion, int index, String option) {
		String[] values = completion.value();
		String val = values[index];
		try {
			if (val.matches(PLAYER.id)) {
				if (Bukkit.getPlayer(option) == null) {
					sender.sendMessage(ChatColor.RED + "Invalid parameters. This player doesn't exist.");
					return false;
				}
			} else if (val.matches(RANGE.id)) {
				Integer.valueOf(option);
			} else if (val.matches(MATERIAL.id)) {
				boolean anyMatch = Arrays.stream(Material.values())
						.anyMatch(e -> e.name().equals(option.toUpperCase()));
				if (!anyMatch) {
					sender.sendMessage(ChatColor.RED + "Invalid parameters. This type of material doesn't exist.");
					return false;
				}
			} else if (val.equals(BOOLEAN.id)) {
				return Boolean.getBoolean(option);
			} else if (val.equals(SOUND.id)) {
				Sound.valueOf(option.toUpperCase());
			} else if (val.equals(WORLD.id)) {
				return Bukkit.getWorld(option) != null;
			} else if (val.equals(ENTITY.id)) {
				boolean anyMatch = Arrays.stream(EntityType.values())
						.anyMatch(e -> e.name().equals(option.toUpperCase()));
				if (!anyMatch) {
					sender.sendMessage(ChatColor.RED + "Invalid parameters. This entity type doesn't exist");
					return false;
				}
			} else if (Register.customOptions.containsKey(val)) {
				List<String> options = Register.customOptions.get(val);
				if (!options.contains(option)) {
					sender.sendMessage(ChatColor.RED + "Sorry, but that's an invalid parameter.");
					return false;
				}
			}
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Invalid parameters. It needs to be a number.");
			return false;
		}
		return true;
	}
}

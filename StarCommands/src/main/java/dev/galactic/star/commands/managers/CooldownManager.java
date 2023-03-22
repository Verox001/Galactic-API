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

import dev.galactic.star.commands.annotations.Cooldown;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
	private static final HashMap<String, Long> cooldowns = new HashMap<>();

	/**
	 * Adds the CommandSender to the cooldown list.
	 *
	 * @param sender   CommandSender.
	 * @param cooldown Cooldown annotation.
	 */
	public void add(CommandSender sender, Cooldown cooldown) {
		if (cooldown == null) {
			return;
		}
		String uuid = this.getUuid(sender);
		if (!cooldown.consoleToo() && uuid.equals("CONSOLE")) return;
		String permission = cooldown.bypassPerm();
		if (!permission.isEmpty() && !uuid.equals("CONSOLE") && sender.hasPermission(permission)) return;
		CooldownManager.cooldowns.put(uuid, this.getMsTime(cooldown.time(), cooldown.unit()));
	}

	/**
	 * Checks whether the sender is in the cooldown list.
	 *
	 * @param sender CommandSender.
	 * @return True if they are, false if not.
	 */
	public boolean isInCoolDown(CommandSender sender) {
		String uuid = this.getUuid(sender);
		if (cooldowns.containsKey(uuid)) {
			long secondsLeft = (CooldownManager.cooldowns.get(uuid) - System.currentTimeMillis()) / 1000;
			if (secondsLeft > 0) {
				sender.sendMessage(String.format(
						"%sPlease wait %ds before you use this command again.",
						ChatColor.RED,
						this.getRemainingSec(sender)
				));
				return true;
			} else {
				CooldownManager.cooldowns.remove(uuid);
			}
		}
		return false;
	}

	/**
	 * Returns the remaining seconds till the cooldown is done.
	 *
	 * @param sender CommandSender.
	 * @return Seconds.
	 */
	public long getRemainingSec(CommandSender sender) {
		return (CooldownManager.cooldowns.get(this.getUuid(sender)) - System.currentTimeMillis()) / 1000;
	}

	private String getUuid(CommandSender sender) {
		return sender instanceof Player ? ((Player) sender).getUniqueId().toString() : "CONSOLE";
	}

	private long getMsTime(long time, TimeUnit unit) {
		long currentTime = System.currentTimeMillis();
		switch (unit) {
			case SECONDS: {
				currentTime += (time * 1000);
				break;
			}
			case MINUTES: {
				currentTime += (time * 60000);
				break;
			}
			default: {
				break;
			}
		}
		return currentTime;
	}

	/**
	 * Gets the Cooldown annotation if specified.
	 *
	 * @param c Class to check for.
	 * @return Cooldown annotation.
	 */
	public Cooldown getCooldown(Class<?> c) {
		return c.getDeclaredAnnotation(Cooldown.class);
	}
}

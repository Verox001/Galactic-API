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

package dev.galactic.star.commands.example;

import dev.galactic.star.commands.annotations.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Command(value = "punish", desc = "Punish a user", usage = "/punish <ban|kick|mute> <player> <reason> <days>",
		aliases = {"a", "b"})
@Permission("Commands.Admin.Punish")
public class BanCommand {

	@SubCommand(value = "ban", usage = "/punish ban <player> <reason> <days>")
	@TabCompletion({".player", ".empty", ".range(1-10)"})
	@Permission(value = "Commands.Admin.Punish.Ban")
	public void ban(CommandSender sender, Player player, String reason, Integer days) {
		sender.sendMessage(player.getDisplayName() + " BANNED FOR: " + reason + " FOR " + days);
	}

	@SubCommand(value = "kick", usage = "/punish kick <player> <reason>")
	@TabCompletion({".player", ".range(0-10)"})
	public void kick(CommandSender sender, Player player, @OptionalArgs Integer reason) {
		sender.sendMessage(player.getDisplayName() + " KICKED FOR: " + reason);
	}

	@SubCommand(value = "mute", usage = "/punish mute <player> <reason>")
	@TabCompletion({".player", ".empty"})
	public void mute(CommandSender sender, Player player, String reason) {
		sender.sendMessage(player.getDisplayName() + " MUTED FOR: " + reason);
	}
}

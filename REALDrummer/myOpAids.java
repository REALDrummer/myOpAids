package REALDrummer;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.craftbukkit.v1_5_R3.block.CraftCreatureSpawner;

import REALDrummer.myGuardDog;

public class myOpAids extends JavaPlugin implements Listener {

	public static Server server;
	public static ConsoleCommandSender console;
	private static String[] parameters = new String[0];
	private static final String[] enable_messages = { "I'm ready to help!", "You're welcome.", "Time to be the best server op ever...other than REALDrummer, of course." },
			disable_messages = { "I hope you enjoyed my BANHAMMER!", "Griefers: banned. \nPlayers: happy. \nYou: happy. \nMe: done with my work here." }, magic_words = {
					"Sha-ZAM!", "ALAKAZAM!", "POOF!", "BOOM!", "KA-POW!", "Sha-FWAAAH!", "Kali-kaPOW!", "TORTELLINI!", "Kras-TOPHALEMOTZ!", "Wah-SHAM!", "Wa-ZAM!",
					"Wha-ZOO!", "KERFUFFLE!", "WOOOOWOWOWOWOW!", "CREAMPUFF WADLEEDEE!", "FLUFFENNUGGET!", "FALALALALAAAAAA-lala-la-LAAAA!", "SHNITZ-LIEDERHOSEN!",
					"BWAAAAAAAAAAAAH!", "FEE-FI-FO-FUM!", "ROTISSERIE!", "LALA-BIBIAY!", "Kurlaka-FWAH!" }, enchantment_level_roman_numerals = { " I", " II", " III", " IV",
					" V" };
	private static HashMap<Enchantment, String> enchantment_names = new HashMap<Enchantment, String>();
	private static HashMap<String, GameMode> offline_player_gamemodes = new HashMap<String, GameMode>(), gamemodes_to_change = new HashMap<String, GameMode>();
	private ArrayList<String> suicidal_maniacs = new ArrayList<String>(), players_who_need_to_input_passwords = new ArrayList<String>();

	// TODO: make it announce when a player is kicked or banned vs. just disconnected (if myScribe doesn't exist, otherwise, myScribe will take care of it)
	// TODO: make a configurable setup where people can determine which plugin is dominant when two plugins have conflicting commands

	// plugin enable/disable and the command operator
	public void onEnable() {
		server = getServer();
		console = server.getConsoleSender();
		// register this class as a listener
		server.getPluginManager().registerEvents(this, this);
		// input enchantment names
		enchantment_names.put(Enchantment.ARROW_DAMAGE, "Power");
		enchantment_names.put(Enchantment.ARROW_FIRE, "Flame");
		enchantment_names.put(Enchantment.ARROW_INFINITE, "Infinite");
		enchantment_names.put(Enchantment.ARROW_KNOCKBACK, "Punch");
		enchantment_names.put(Enchantment.DAMAGE_ALL, "Sharpness");
		enchantment_names.put(Enchantment.DAMAGE_ARTHROPODS, "Bane of Arthropods");
		enchantment_names.put(Enchantment.DAMAGE_UNDEAD, "Smite");
		enchantment_names.put(Enchantment.DIG_SPEED, "Efficiency");
		enchantment_names.put(Enchantment.DURABILITY, "Unbreaking");
		enchantment_names.put(Enchantment.FIRE_ASPECT, "Fire Aspect");
		enchantment_names.put(Enchantment.KNOCKBACK, "Knockback");
		enchantment_names.put(Enchantment.LOOT_BONUS_BLOCKS, "Fortune");
		enchantment_names.put(Enchantment.LOOT_BONUS_MOBS, "Looting");
		enchantment_names.put(Enchantment.OXYGEN, "Respiration");
		enchantment_names.put(Enchantment.PROTECTION_ENVIRONMENTAL, "Protection");
		enchantment_names.put(Enchantment.PROTECTION_EXPLOSIONS, "Blast Protection");
		enchantment_names.put(Enchantment.PROTECTION_FALL, "Feather Falling");
		enchantment_names.put(Enchantment.PROTECTION_FIRE, "Fire Protection");
		enchantment_names.put(Enchantment.PROTECTION_PROJECTILE, "Projectile Protection");
		enchantment_names.put(Enchantment.SILK_TOUCH, "Silk Touch");
		enchantment_names.put(Enchantment.WATER_WORKER, "Aqua Affinity");
		// done enabling
		String enable_message = enable_messages[(int) (Math.random() * enable_messages.length)];
		console.sendMessage(ChatColor.GRAY + enable_message);
		for (Player player : server.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(ChatColor.GRAY + enable_message);
	}

	public void onDisable() {
		// sync gamemode data with myGuardDog if they have it

		// done disabling
		String disable_message = disable_messages[(int) (Math.random() * disable_messages.length)];
		console.sendMessage(ChatColor.GRAY + disable_message);
		for (Player player : server.getOnlinePlayers())
			if (player.isOp())
				player.sendMessage(ChatColor.GRAY + disable_message);
	}

	public boolean onCommand(CommandSender sender, Command cmd, String command, String[] my_parameters) {
		parameters = my_parameters;
		if (command.equalsIgnoreCase("enchant") || command.equalsIgnoreCase("ench")) {
			if (!(sender instanceof Player))
				sender.sendMessage(ChatColor.RED
						+ "Okay. Sure. Let me just...-_- You're a console. You know you're not holding any enchantable items right? ...'cause you have no hands? ...'cause YOU'RE A CONSOLE!?");
			else if (!sender.isOp())
				sender.sendMessage(ChatColor.RED
						+ "Sorry, but you don't have permission to enchant items at your whim. You have to kill monsters and mine and earn those levels just like everyone else.");
			else if (parameters.length == 0)
				sender.sendMessage(ChatColor.RED + "You forgot to tell me what enchantment you want to put on it!");
			else
				enchantItem(sender);
			return true;
		} else if (command.equalsIgnoreCase("enable") || command.equalsIgnoreCase("en")) {
			if (sender instanceof Player && !sender.isOp())
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to enable and disable plugins.");
			else if (parameters.length == 0)
				sender.sendMessage(ChatColor.RED + "You forgot to tell me which plugin to enable!");
			else {
				for (Plugin target : server.getPluginManager().getPlugins())
					if (target.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (!target.isEnabled()) {
							server.getPluginManager().enablePlugin(target);
							sender.sendMessage(ChatColor.GRAY + target.getName() + " has been enabled.");
						} else
							sender.sendMessage(ChatColor.RED + target.getName() + " is already enabled.");
						return true;
					}
			}
			sender.sendMessage(ChatColor.RED + "Sorry, but I couldn't find a plugin called \"" + parameters[0] + ".\"");
			return true;
		} else if (command.equalsIgnoreCase("disable") || command.equalsIgnoreCase("dis")) {
			if (sender instanceof Player && !sender.isOp())
				sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to enable and disable plugins.");
			else if (parameters.length == 0)
				sender.sendMessage(ChatColor.RED + "You forgot to tell me which plugin to disable!");
			else {
				for (Plugin target : server.getPluginManager().getPlugins())
					if (target.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (target.isEnabled()) {
							server.getPluginManager().disablePlugin(target);
							sender.sendMessage(ChatColor.GRAY + target.getName() + " has been disabled.");
						} else
							sender.sendMessage(ChatColor.RED + target.getName() + " is already disabled.");
						return true;
					}
			}
			sender.sendMessage(ChatColor.RED + "Sorry, but I couldn't find a plugin called \"" + parameters[0] + ".\"");
			return true;
		} else if (command.equalsIgnoreCase("kill") || command.equalsIgnoreCase("murder")) {
			if (!(sender instanceof Player) && parameters.length == 0)
				sender.sendMessage(ChatColor.RED + "Ouch. That must have hurt. You're immortal, though. You're a console. If you really want to die that badly, try /stop.");
			else if (parameters.length == 0 || !sender.isOp()) {
				((Player) sender).setHealth(0);
				sender.sendMessage(ChatColor.GRAY + "" + ChatColor.ITALIC + "Noooooooooooo!!!\nWhy did it have to end like this?!");
				suicidal_maniacs.add(sender.getName());
			} else {
				for (Player target : server.getOnlinePlayers())
					if (target.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						target.setHealth(0);
						if (sender instanceof Player)
							target.sendMessage(ChatColor.GRAY + sender.getName() + " killed you.");
						else
							target.sendMessage(ChatColor.GRAY + "Someone killed you.");
						return true;
					}
				sender.sendMessage(ChatColor.RED + "There's no one online named \"" + parameters[0] + ".\"");
			}
			return true;
		} else if (command.equalsIgnoreCase("gamemode") || command.equalsIgnoreCase("gm")) {
			if (!(sender instanceof Player) || sender.isOp() || sender.hasPermission("myopaids.admin"))
				changeGameMode(sender);
			else
				sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change gamemodes.");
			return true;
		} else if (command.equalsIgnoreCase("ids") || command.equalsIgnoreCase("id")) {
			id(sender);
			return true;
		}
		return false;
	}

	// listeners
	@EventHandler
	public void rejoiceAtThePlayersRespawnAfterSuicide(PlayerRespawnEvent event) {
		if (suicidal_maniacs.contains(event.getPlayer().getName())) {
			suicidal_maniacs.remove(event.getPlayer().getName());
			event.getPlayer().sendMessage(
					ChatColor.GRAY + "..." + event.getPlayer().getName().substring(0, 1).toUpperCase() + event.getPlayer().getName().substring(1) + "? Is that really you?\n"
							+ ChatColor.ITALIC + "You're not dead! Yaaaaaaaaaay!!!!!!");
		}
	}

	@EventHandler
	public void recordThePlayersGameModeBeforeTheyLogOff(PlayerQuitEvent event) {
		offline_player_gamemodes.put(event.getPlayer().getName(), event.getPlayer().getGameMode());
	}

	@EventHandler
	public void cancelMonsterCombatEngagesForPlayersWhoNeedToPutInTheServerPassword(EntityTargetEvent event) {
		// TODO make a entity stop targeting a player who is putting in the password.
	}

	@EventHandler
	public void makeMonsterSpawnersDropWithSilkTouch(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("myopaids.spawners")
				&& event.getPlayer().getGameMode() != GameMode.CREATIVE
				&& event.getBlock().getTypeId() == 52
				&& (event.getPlayer().getItemInHand().getType() == Material.DIAMOND_PICKAXE || event.getPlayer().getItemInHand().getType() == Material.IRON_PICKAXE
						|| event.getPlayer().getItemInHand().getType() == Material.GOLD_PICKAXE || event.getPlayer().getItemInHand().getType() == Material.STONE_PICKAXE || event
						.getPlayer().getItemInHand().getType() == Material.WOOD_PICKAXE) && event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
			// make sure it doesn't drop xp; if it did, people could just place and break spawners and get xp for free
			event.setExpToDrop(0);
			short id = ((CraftCreatureSpawner) event.getBlock().getState()).getSpawnedType().getTypeId();
			String mob_name = myPluginWiki.getEntityName(id, 0, false, true);
			// eliminate the article at the beginning of the name
			mob_name = mob_name.substring(mob_name.indexOf(" ") + 1);
			// construct the mob spawner item
			ItemStack item = new ItemStack(Material.MOB_SPAWNER, 1, id);
			ItemMeta metadata = item.getItemMeta();
			metadata.setDisplayName(mob_name + " spawner");
			item.setItemMeta(metadata);
			event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), item);
		}
	}

	@EventHandler
	public void fixMonstersSpawnersWhenTheyArePlaced(BlockPlaceEvent event) {
		String display_name = event.getPlayer().getItemInHand().getItemMeta().getDisplayName();
		if (event.getPlayer().getItemInHand().getTypeId() == 52 && !display_name.equals("Monster Spawner")) {
			// use .substring() to remove the "spawner" at the end of the name
			Integer id = myPluginWiki.getEntityIdAndData(display_name.substring(0, display_name.length() - 8))[0];
			if (id != null) {
				((CraftCreatureSpawner) event.getBlock().getState()).setSpawnedType(EntityType.fromId(id));
				event.getBlock().getState().update(true);
			} else {
				event.setCancelled(true);
				event.getPlayer()
						.sendMessage(ChatColor.RED + "Wait. ...Uh...don't place that. Sorry. I'm confused. Tell your admin to check the console. Something is wrong.");
				console.sendMessage(ChatColor.DARK_RED + "What the heck is a " + display_name + "? I've never heard of a \""
						+ display_name.substring(0, display_name.length() - 8) + "\".");
			}
		}
	}

	// plugin commands
	private void changeGameMode(CommandSender sender) {
		if (server.getPluginManager().getPlugin("myGuardDog") != null) {
			myGuardDog.parameters = parameters;
			myGuardDog.changeGameMode(sender);
			return;
		}
		if (parameters.length == 0)
			if (sender instanceof Player)
				if (((Player) sender).getGameMode() == GameMode.CREATIVE) {
					((Player) sender).setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(ChatColor.GRAY + "You're now in Survival Mode. Watch out for monsters.");
					console.sendMessage(ChatColor.GRAY + sender.getName() + " changed to Survival Mode.");
				} else {
					((Player) sender).setGameMode(GameMode.CREATIVE);
					sender.sendMessage(ChatColor.GRAY + "You're now in Creative Mode. Go nuts. Have fun.");
					console.sendMessage(ChatColor.GRAY + sender.getName() + " changed to Creative Mode.");
				}
			else
				sender.sendMessage(ChatColor.RED + "You forgot to tell me whose gamemode you want me to change! I can't exactly change yours, can I?");
		else if (parameters.length == 1)
			if (sender instanceof Player && ("creative".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("1"))) {
				if (!((Player) sender).getGameMode().equals(GameMode.CREATIVE)) {
					((Player) sender).setGameMode(GameMode.CREATIVE);
					sender.sendMessage(ChatColor.GRAY + "You're now in Creative Mode. Go nuts. Have fun.");
					console.sendMessage(ChatColor.GRAY + sender.getName() + " changed to Creative Mode.");
				} else
					sender.sendMessage(ChatColor.RED + "You're already in Creative Mode!");
			} else if (sender instanceof Player && ("survival".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("0"))) {
				if (!((Player) sender).getGameMode().equals(GameMode.SURVIVAL)) {
					((Player) sender).setGameMode(GameMode.SURVIVAL);
					sender.sendMessage(ChatColor.GRAY + "You're now in Survival Mode. Watch out for monsters.");
					console.sendMessage(ChatColor.GRAY + sender.getName() + " changed to Survival Mode.");
				} else
					sender.sendMessage(ChatColor.RED + "You're already in Survival Mode!");
			} else {
				for (Player player : server.getOnlinePlayers())
					if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
							sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to change other ops' gamemodes.");
						else if (player.getGameMode().equals(GameMode.SURVIVAL)) {
							player.setGameMode(GameMode.CREATIVE);
							if (sender instanceof Player) {
								player.sendMessage(ChatColor.GRAY + sender.getName() + " put you in Creative Mode.");
								sender.sendMessage(ChatColor.GRAY + "You put " + player.getName() + " in Creative Mode.");
								console.sendMessage(ChatColor.GRAY + sender.getName() + " put " + player.getName() + " in Creative Mode.");
							} else {
								player.sendMessage(ChatColor.GRAY + "Someone put you in Creative Mode from the console.");
								console.sendMessage(ChatColor.GRAY + "You put " + player.getName() + " in Creative Mode.");
							}
						} else {
							player.setGameMode(GameMode.SURVIVAL);
							if (sender instanceof Player) {
								player.sendMessage(ChatColor.GRAY + sender.getName() + " put you in Survival Mode.");
								sender.sendMessage(ChatColor.GRAY + "You put " + player.getName() + " in Survival Mode.");
								console.sendMessage(ChatColor.GRAY + sender.getName() + " put " + player.getName() + " in Survival Mode.");
							} else {
								player.sendMessage(ChatColor.GRAY + "Someone put you in Survival Mode from the console.");
								console.sendMessage(ChatColor.GRAY + "You put " + player.getName() + " in Survival Mode.");
							}
						}
						return;
					}
				for (OfflinePlayer player : server.getOfflinePlayers())
					if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
						if (player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
							sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change other people's gamemodes.");
						else if (offline_player_gamemodes.get(player.getName()) == null)
							sender.sendMessage(ChatColor.RED
									+ "Sorry, but I don't remember what "
									+ player.getName()
									+ "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
						else if (offline_player_gamemodes.get(player.getName()).equals(GameMode.SURVIVAL)) {
							gamemodes_to_change.put(player.getName(), GameMode.CREATIVE);
							sender.sendMessage(ChatColor.GRAY + "I'll put " + player.getName() + " in Creative Mode when they get back online.");
						} else {
							gamemodes_to_change.put(player.getName(), GameMode.SURVIVAL);
							sender.sendMessage(ChatColor.GRAY + "I'll put " + player.getName() + " in Survival Mode when they get back online.");
						}
						return;
					}
				sender.sendMessage(ChatColor.RED + "Sorry, but I don't believe anyone named \"" + parameters[0] + "\" has ever played on this server.");
			}
		else {
			String player;
			GameMode new_gamemode = null;
			if (parameters[0].equals("1") || "creative".startsWith(parameters[0].toLowerCase())) {
				player = parameters[1];
				new_gamemode = GameMode.CREATIVE;
			} else if (parameters[0].equals("0") || "survival".startsWith(parameters[0].toLowerCase())) {
				player = parameters[1];
				new_gamemode = GameMode.SURVIVAL;
			} else if (parameters[1].equals("1") || "creative".startsWith(parameters[1].toLowerCase())) {
				player = parameters[0];
				new_gamemode = GameMode.CREATIVE;
			} else if (parameters[1].equals("0") || "survival".startsWith(parameters[1].toLowerCase())) {
				player = parameters[0];
				new_gamemode = GameMode.SURVIVAL;
			} else
				player = parameters[0];
			for (Player online_player : server.getOnlinePlayers())
				if (online_player.getName().toLowerCase().startsWith(player.toLowerCase())) {
					if (online_player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
						sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to change other ops' gamemodes.");
					else {
						if (new_gamemode != null)
							online_player.setGameMode(new_gamemode);
						else if (online_player.getGameMode().equals(GameMode.SURVIVAL)) {
							online_player.setGameMode(GameMode.CREATIVE);
							new_gamemode = GameMode.CREATIVE;
						} else {
							online_player.setGameMode(GameMode.SURVIVAL);
							new_gamemode = GameMode.SURVIVAL;
						}
						if (sender instanceof Player) {
							online_player.sendMessage(ChatColor.GRAY + sender.getName() + " put you in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
							sender.sendMessage(ChatColor.GRAY + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
							console.sendMessage(ChatColor.GRAY + sender.getName() + " put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
						} else {
							online_player.sendMessage(ChatColor.GRAY + "Someone put you in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode from the console.");
							console.sendMessage(ChatColor.GRAY + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
									+ new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
						}
					}
					return;
				}
			for (OfflinePlayer offline_player : server.getOfflinePlayers())
				if (offline_player.getName().toLowerCase().startsWith(player)) {
					if (offline_player.isOp() && sender instanceof Player && !sender.hasPermission("myopaids.admin"))
						sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change other people's gamemodes.");
					else if (new_gamemode != null) {
						gamemodes_to_change.put(offline_player.getName(), new_gamemode);
						sender.sendMessage(ChatColor.GRAY + "I'll put " + offline_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
								+ new_gamemode.toString().substring(1).toLowerCase() + " Mode when they get back online.");
					} else if (offline_player_gamemodes.get(offline_player.getName()) == null)
						sender.sendMessage(ChatColor.RED
								+ "Sorry, but I don't remember what "
								+ offline_player.getName()
								+ "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
					else if (offline_player_gamemodes.get(offline_player.getName()).equals(GameMode.SURVIVAL)) {
						gamemodes_to_change.put(offline_player.getName(), GameMode.CREATIVE);
						sender.sendMessage(ChatColor.GRAY + "I'll put " + offline_player.getName() + " in Creative Mode when they get back online.");
					} else {
						gamemodes_to_change.put(offline_player.getName(), GameMode.SURVIVAL);
						sender.sendMessage(ChatColor.GRAY + "I'll put " + offline_player.getName() + " in Survival Mode when they get back online.");
					}
					return;
				}
			sender.sendMessage(ChatColor.RED + "Sorry, but I don't believe anyone named \"" + parameters[0] + "\" has ever played on this server.");
		}
	}

	private void enchantItem(CommandSender sender) {
		Player player = (Player) sender;
		int id = player.getItemInHand().getTypeId();
		String item_name = myPluginWiki.getItemName(id, -1, false, false, true);
		// check to make sure the item is enchantable at all
		if (!(id == 256 || id == 257 || id == 258 || id == 261 || (id >= 267 && id <= 279) || (id >= 283 && id <= 286) || (id >= 302 && id <= 317))) {
			player.sendMessage(ChatColor.RED + "Sorry, but " + item_name + " aren't even enchantable.");
			return;
		}
		// read the enchantments to add
		String temp = "";
		for (String parameter : parameters)
			temp = temp + parameter;
		String[] enchantments_names = temp.split(",");
		int[] enchantments_levels = new int[enchantments_names.length];
		// get the level of each enchantment
		for (int i = 0; i < enchantments_names.length; i++)
			if (enchantments_names[i].toLowerCase().endsWith("iv")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 2);
				enchantments_levels[i] = 4;
			} else if (enchantments_names[i].toLowerCase().endsWith("v")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 5;
			} else if (enchantments_names[i].toLowerCase().endsWith("iii")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 3);
				enchantments_levels[i] = 3;
			} else if (enchantments_names[i].toLowerCase().endsWith("ii")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 2);
				enchantments_levels[i] = 2;
			} else if (enchantments_names[i].toLowerCase().endsWith("i")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 1;
			} else if (enchantments_names[i].toLowerCase().endsWith("5")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 5;
			} else if (enchantments_names[i].toLowerCase().endsWith("4")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 4;
			} else if (enchantments_names[i].toLowerCase().endsWith("3")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 3;
			} else if (enchantments_names[i].toLowerCase().endsWith("2")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 2;
			} else if (enchantments_names[i].toLowerCase().endsWith("1")) {
				enchantments_names[i] = enchantments_names[i].substring(0, enchantments_names[i].length() - 1);
				enchantments_levels[i] = 1;
			} else
				enchantments_levels[i] = 1;
		// get the enchantments themselves
		Enchantment[] enchantments = new Enchantment[enchantments_names.length];
		for (int i = 0; i < enchantments_names.length; i++)
			for (Object enchantment : enchantment_names.keySet().toArray())
				if (enchantment_names.get(enchantment).toLowerCase().startsWith(enchantments_names[i].toLowerCase())
						&& ((Enchantment) enchantment).canEnchantItem(player.getItemInHand()))
					enchantments[i] = ((Enchantment) enchantment);
		for (int i = 0; i < enchantments_names.length; i++)
			if (enchantments[i] == null)
				for (Object enchantment : enchantment_names.keySet().toArray())
					if (enchantment_names.get(enchantment).toLowerCase().startsWith(enchantments_names[i].toLowerCase()))
						enchantments[i] = ((Enchantment) enchantment);
		// try to enchant the item
		ArrayList<Enchantment> good_enchantments = new ArrayList<Enchantment>(), bad_enchantments = new ArrayList<Enchantment>(), mislevelled_enchantments =
				new ArrayList<Enchantment>();
		ArrayList<String> absent_enchantments = new ArrayList<String>(), good_enchantments_levels = new ArrayList<String>(), mislevelled_enchantments_levels =
				new ArrayList<String>();
		for (int i = 0; i < enchantments.length; i++) {
			if (enchantments[i] != null && enchantments[i].canEnchantItem(player.getItemInHand()) && enchantments[i].getMaxLevel() >= enchantments_levels[i]) {
				player.getItemInHand().addEnchantment(enchantments[i], enchantments_levels[i]);
				good_enchantments.add(enchantments[i]);
				// if an enchantment can't have more than one level, just
				// eliminate the level from the name
				if (enchantments[i].getMaxLevel() == 1)
					good_enchantments_levels.add("");
				else
					good_enchantments_levels.add(enchantment_level_roman_numerals[enchantments_levels[i] - 1]);
			} else if (enchantments[i] != null && enchantments[i].canEnchantItem(player.getItemInHand()))
				bad_enchantments.add(enchantments[i]);
			else if (enchantments[i] != null) {
				mislevelled_enchantments.add(enchantments[i]);
				if (enchantments[i].getMaxLevel() == 1)
					mislevelled_enchantments_levels.add("");
				else
					// make sure that if the level is over 5, it just uses the
					// digits since there is no logged roman numeral for
					// anything over 5
					try {
						mislevelled_enchantments_levels.add(enchantment_level_roman_numerals[enchantments_levels[i] - 1]);
					} catch (ArrayIndexOutOfBoundsException exception) {
						mislevelled_enchantments_levels.add(" " + enchantments_levels[i]);
					}
			} else
				absent_enchantments.add(enchantments_names[i]);
		}
		// display results
		if (good_enchantments.size() == 1)
			player.sendMessage(ChatColor.GOLD + magic_words[(int) (Math.random() * magic_words.length)] + ChatColor.GRAY + " Your " + item_name + " is now enchanted with "
					+ ChatColor.GRAY + enchantment_names.get(good_enchantments.get(0)) + good_enchantments_levels.get(0) + ChatColor.GRAY + ".");
		else if (good_enchantments.size() == 2)
			player.sendMessage(ChatColor.GOLD + magic_words[(int) (Math.random() * magic_words.length)] + ChatColor.GRAY + " Your " + item_name + " is now enchanted with "
					+ ChatColor.GRAY + enchantment_names.get(good_enchantments.get(0)) + good_enchantments_levels.get(0) + ChatColor.GRAY + " and " + ChatColor.GRAY
					+ enchantment_names.get(good_enchantments.get(1)) + good_enchantments_levels.get(1) + ChatColor.GRAY + ".");
		else if (good_enchantments.size() > 2) {
			String message_beginning =
					ChatColor.GOLD + magic_words[(int) (Math.random() * magic_words.length)] + ChatColor.GRAY + " Your " + item_name + " is now enchanted with ";
			for (int i = 0; i < good_enchantments.size() - 1; i++)
				message_beginning =
						message_beginning + ChatColor.GRAY + enchantment_names.get(good_enchantments.get(i)) + good_enchantments_levels.get(i) + ChatColor.GRAY + ", ";
			player.sendMessage(message_beginning + "and " + ChatColor.GRAY + enchantment_names.get(good_enchantments.get(good_enchantments.size() - 1))
					+ good_enchantments_levels.get(good_enchantments_levels.size() - 1) + ChatColor.GRAY + ".");
		}
		if (bad_enchantments.size() > 0) {
			String message_beginning = ChatColor.RED + "";
			if (good_enchantments.size() > 0)
				message_beginning = ChatColor.RED + "However, ";
			if (bad_enchantments.size() == 1)
				player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(bad_enchantments.get(0)) + ChatColor.RED + " doesn't work on " + item_name
						+ "s. Sorry.");
			else if (bad_enchantments.size() == 2)
				player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(bad_enchantments.get(0)) + ChatColor.RED + " and " + ChatColor.GRAY
						+ enchantment_names.get(bad_enchantments.get(1)) + ChatColor.RED + " don't work on " + item_name + "s. Sorry.");
			else if (bad_enchantments.size() > 2) {
				for (int i = 0; i < bad_enchantments.size() - 1; i++)
					message_beginning = message_beginning + ChatColor.GRAY + enchantment_names.get(bad_enchantments.get(i)) + ChatColor.RED + ", ";
				player.sendMessage(message_beginning + "and " + ChatColor.GRAY + enchantment_names.get(bad_enchantments.get(bad_enchantments.size() - 1)) + ChatColor.RED
						+ " don't work on " + item_name + "s. Sorry.");
			}
		}
		if (mislevelled_enchantments.size() > 0) {
			String message_beginning = ChatColor.RED + "";
			if (bad_enchantments.size() > 0)
				message_beginning = ChatColor.RED + "Also, ";
			else if (good_enchantments.size() > 0)
				message_beginning = ChatColor.RED + "However, ";
			if (mislevelled_enchantments.size() == 1)
				player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(0)) + ChatColor.RED + " doesn't have a level "
						+ mislevelled_enchantments_levels.get(0) + ". It only goes up to level"
						+ enchantment_level_roman_numerals[mislevelled_enchantments.get(0).getMaxLevel() - 1] + ".");
			else if (mislevelled_enchantments.size() == 2)
				if (mislevelled_enchantments_levels.get(0) == mislevelled_enchantments_levels.get(1)
						&& mislevelled_enchantments.get(0).getMaxLevel() == mislevelled_enchantments.get(1).getMaxLevel())
					player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(0)) + ChatColor.RED + " and " + ChatColor.GRAY
							+ enchantment_names.get(mislevelled_enchantments.get(1)) + ChatColor.RED + " don't have a level " + mislevelled_enchantments_levels.get(0)
							+ ". They only go up to" + enchantment_level_roman_numerals[mislevelled_enchantments.get(0).getMaxLevel() - 1] + ".");
				else if (mislevelled_enchantments_levels.get(0) == mislevelled_enchantments_levels.get(1))
					player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(0)) + ChatColor.RED + " and " + ChatColor.GRAY
							+ enchantment_names.get(mislevelled_enchantments.get(1)) + ChatColor.RED + " don't have a level " + mislevelled_enchantments_levels.get(0)
							+ ". They only go up to" + enchantment_level_roman_numerals[mislevelled_enchantments.get(0).getMaxLevel() - 1] + " and"
							+ enchantment_level_roman_numerals[mislevelled_enchantments.get(1).getMaxLevel() - 1] + ", respectively.");
				else if (mislevelled_enchantments.get(0).getMaxLevel() == mislevelled_enchantments.get(1).getMaxLevel())
					player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(0)) + ChatColor.RED + " and " + ChatColor.GRAY
							+ enchantment_names.get(mislevelled_enchantments.get(1)) + ChatColor.RED + " don't have a level" + mislevelled_enchantments_levels.get(0) + " or"
							+ mislevelled_enchantments_levels.get(1) + ", respectively. They only go up to"
							+ enchantment_level_roman_numerals[mislevelled_enchantments.get(0).getMaxLevel() - 1] + ".");
				else
					player.sendMessage(message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(0)) + ChatColor.RED + " and " + ChatColor.GRAY
							+ enchantment_names.get(mislevelled_enchantments.get(1)) + ChatColor.RED + " don't have a level" + mislevelled_enchantments_levels.get(0) + " or"
							+ mislevelled_enchantments_levels.get(1) + ", respectively. They only go up to"
							+ enchantment_level_roman_numerals[mislevelled_enchantments.get(0).getMaxLevel() - 1] + " and"
							+ enchantment_level_roman_numerals[mislevelled_enchantments.get(1).getMaxLevel() - 1] + ", respectively.");
			else if (mislevelled_enchantments.size() > 2) {
				for (int i = 0; i < mislevelled_enchantments.size() - 1; i++)
					message_beginning = message_beginning + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(i)) + ChatColor.RED + ", ";
				message_beginning =
						message_beginning + "and " + ChatColor.GRAY + enchantment_names.get(mislevelled_enchantments.get(mislevelled_enchantments.size() - 1)) + ChatColor.RED
								+ " don't have a level ";
				for (int i = 0; i < mislevelled_enchantments_levels.size() - 1; i++)
					message_beginning = message_beginning + mislevelled_enchantments_levels.get(i) + ", ";
				message_beginning =
						message_beginning + "or" + mislevelled_enchantments_levels.get(mislevelled_enchantments.size() - 1) + ", respectively. They can only go up to ";
				for (int i = 0; i < mislevelled_enchantments.size() - 1; i++)
					message_beginning = message_beginning + enchantment_level_roman_numerals[mislevelled_enchantments.get(i).getMaxLevel() - 1] + ", ";
				player.sendMessage(message_beginning + "or"
						+ enchantment_level_roman_numerals[mislevelled_enchantments.get(mislevelled_enchantments.size() - 1).getMaxLevel() - 1] + ", respectively.");
			}
		}
		if (absent_enchantments.size() > 0) {
			String message_beginning = ChatColor.RED + "I can't find any enchantments at all that start with \"";
			if (bad_enchantments.size() > 0 || mislevelled_enchantments.size() > 0)
				message_beginning = ChatColor.RED + "Also, I can't find any enchantments at all that start with \"";
			else if (good_enchantments.size() > 0)
				message_beginning = ChatColor.RED + "However, I can't find any enchantments at all that start with \"";
			if (absent_enchantments.size() == 1)
				player.sendMessage(message_beginning + absent_enchantments.get(0) + ".\"");
			else if (absent_enchantments.size() == 2)
				player.sendMessage(message_beginning + absent_enchantments.get(0) + "\" or \"" + absent_enchantments.get(1) + ".\"");
			else if (absent_enchantments.size() > 2) {
				for (int i = 0; i < absent_enchantments.size() - 1; i++)
					message_beginning = message_beginning + absent_enchantments.get(i) + "\", \"";
				player.sendMessage(message_beginning + " or \"" + absent_enchantments.get(absent_enchantments.size() - 1) + ".\"");
			}
		}
	}

	private void id(CommandSender sender) {
		if (parameters.length == 0 || parameters[0].equalsIgnoreCase("this") || parameters[0].equalsIgnoreCase("that"))
			if (sender instanceof Player) {
				Player player = (Player) sender;
				Block block = player.getTargetBlock(null, 1024);
				String block_name = myPluginWiki.getItemName(block, false, true, true), id_and_data = myPluginWiki.getItemIdAndDataString(block_name, false);
				// send the message
				if (block_name != null)
					player.sendMessage(ChatColor.GRAY + "That " + block_name + " you're pointing at has the I.D. " + id_and_data + ".");
				else {
					player.sendMessage(ChatColor.RED + "Uh...what in the world " + ChatColor.ITALIC + "is" + ChatColor.RED + " that thing you're pointing at?");
					player.sendMessage(ChatColor.RED + "Well, whatever it is, it has the I.D. " + id_and_data + ".");
				}
				String item_name = myPluginWiki.getItemName(player.getItemInHand(), false, player.getItemInHand().getAmount() == 1, true);
				id_and_data = myPluginWiki.getItemIdAndDataString(item_name, true);
				// send the message
				if (item_name != null)
					if (player.getItemInHand().getAmount() > 1)
						player.sendMessage(ChatColor.GRAY + "Those " + item_name + " you're holding have the I.D. " + id_and_data + ".");
					else
						player.sendMessage(ChatColor.GRAY + "That " + item_name + " you're holding has the I.D. " + id_and_data + ".");
				else {
					if (player.getItemInHand().getAmount() > 1)
						player.sendMessage(ChatColor.GRAY + "Uh...what in the world " + ChatColor.ITALIC + "are" + ChatColor.RED + " those things you're holding?");
					else
						player.sendMessage(ChatColor.GRAY + "Uh...what in the world " + ChatColor.ITALIC + "is" + ChatColor.RED + " that thing you're holding?");
					player.sendMessage(ChatColor.RED + "Well, whatever it is, it has the I.D. " + id_and_data + ".");
				}
			} else
				sender.sendMessage(ChatColor.RED + "You forgot to tell me what item or I.D. you want identified!");
		else {
			String query = "";
			for (String parameter : parameters)
				if (query.equals(""))
					query = parameter;
				else
					query += " " + parameter;
			// for simple I.D. queries
			try {
				int id = Integer.parseInt(query);
				String item_name = myPluginWiki.getItemName(id, -1, false, false, true);
				if (item_name != null)
					// if the singular form uses the "some" artcile or the item name ends in "s" but not "ss" (like "wooden planks", but not like
					// "grass"), the item name is a true plural
					if (!myPluginWiki.getItemName(id, -1, false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
						sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id + ".");
					else
						sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id + ".");
				else
					sender.sendMessage(ChatColor.RED + "No item has the I.D. " + id + ".");
			} catch (NumberFormatException exception) {
				try {
					String[] temp = query.split(":");
					if (temp.length == 2) {
						// for "[id]:[data]" queries
						int id = Integer.parseInt(temp[0]), data = Integer.parseInt(temp[1]);
						String item_name = myPluginWiki.getItemName(id, data, false, false, true);
						// send the message
						if (item_name != null)
							// if the singular form uses the "some" artcile or the item name ends in "s" but not "ss" (like "wooden planks", but not like
							// "grass"), the item name is a true plural
							if (!myPluginWiki.getItemName(id, data, false, true, false).startsWith("some ") || (item_name.endsWith("s") && !item_name.endsWith("ss")))
								sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + query + ".");
							else
								sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + query + ".");
						else
							sender.sendMessage(ChatColor.RED + "No item has the I.D. " + query + ".");
					} else {
						// for word queries
						Integer[] id_and_data = myPluginWiki.getItemIdAndData(query, null);
						if (id_and_data == null) {
							if (query.toLowerCase().startsWith("a") || query.toLowerCase().startsWith("e") || query.toLowerCase().startsWith("i")
									|| query.toLowerCase().startsWith("o") || query.toLowerCase().startsWith("u"))
								sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what an \"" + query + "\" is.");
							else
								sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what a \"" + query + "\" is.");
							return;
						}
						// this part seems odd because it seems like it's a long roundabout way to get item_name. You might think: isn't item_name the same as
						// query? Wrong. A query can (and probably is) just a few letters from the name of the item. By finding the id, then using that to get
						// the name, it's an effective autocompletion of the item name.
						String item_name = myPluginWiki.getItemName(id_and_data[0], id_and_data[1], false, false, true), id_and_data_term = String.valueOf(id_and_data[0]);
						if (id_and_data[1] > 0)
							id_and_data_term += ":" + id_and_data[1];
						// if it found it, send the message
						if (!myPluginWiki.getItemName(id_and_data[0], id_and_data[1], false, true, false).startsWith("some ")
								|| (item_name.endsWith("s") && !item_name.endsWith("ss")))
							sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id_and_data_term + ".");
						else
							sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id_and_data_term + ".");
					}
				} catch (NumberFormatException e) {
					// for word queries
					Integer[] id_and_data = myPluginWiki.getItemIdAndData(query, null);
					if (id_and_data == null) {
						if (query.toLowerCase().startsWith("a") || query.toLowerCase().startsWith("e") || query.toLowerCase().startsWith("i")
								|| query.toLowerCase().startsWith("o") || query.toLowerCase().startsWith("u"))
							sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what an \"" + query + "\" is.");
						else
							sender.sendMessage(ChatColor.RED + "Sorry, but I don't know what a \"" + query + "\" is.");
						return;
					}
					// this part seems odd because it seems like it's a long roundabout way to get item_name. You might think: isn't item_name the same as
					// query? Wrong. A query can (and probably is) just a few letters from the name of the item. By finding the id, then using that to get
					// the name, it's an effective autocompletion of the item name.
					String item_name = myPluginWiki.getItemName(id_and_data[0], id_and_data[1], false, false, true), id_and_data_term = String.valueOf(id_and_data[0]);
					if (id_and_data[1] > 0)
						id_and_data_term += ":" + id_and_data[1];
					// if it found it, send the message
					if (!myPluginWiki.getItemName(id_and_data[0], id_and_data[1], false, true, false).startsWith("some ")
							|| (item_name.endsWith("s") && !item_name.endsWith("ss")))
						sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " have the I.D. " + id_and_data_term + ".");
					else
						sender.sendMessage(ChatColor.GRAY + item_name.substring(0, 1).toUpperCase() + item_name.substring(1) + " has the I.D. " + id_and_data_term + ".");
				}
			}
		}
	}
}
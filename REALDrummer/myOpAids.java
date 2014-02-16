package REALDrummer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class myOpAids extends JavaPlugin implements Listener {

    public static Server server;
    public static ConsoleCommandSender console;
    public static final ChatColor COLOR = ChatColor.GRAY;
    private static GregorianCalendar time = new GregorianCalendar();
    public static String[] parameters = new String[0];
    private static final String[] enable_messages = { "I'm ready to help!", "You're welcome.", "Time to be the best server op ever...other than REALDrummer, of course." },
            disable_messages = { "I hope you enjoyed my BANHAMMER!", "Griefers: banned. \nPlayers: happy. \nYou: happy. \nMe: done with my work here." };
    public static int number_of_attempts = 5;
    public static HashMap<String, String> passwords = new HashMap<String, String>(), password_resetters = new HashMap<String, String>();
    public static HashMap<String, GameMode> offline_player_gamemodes = new HashMap<String, GameMode>(), gamemodes_to_change = new HashMap<String, GameMode>();
    public static ArrayList<String> debugging_players = new ArrayList<String>(), suicidal_maniacs = new ArrayList<String>(), relogging_players = new ArrayList<String>();
    public HashMap<String, Integer> password_inputters = new HashMap<String, Integer>();
    public HashMap<String, Long> kick_timers = new HashMap<String, Long>();

    // TODO: make it announce when a player is kicked or banned vs. just disconnected (if myScribe doesn't exist, otherwise, myScribe will take care of it)
    // TODO: make a configurable setup where people can determine which plugin is dominant when two plugins have conflicting commands

    // DONE: added new fishing enchantments
    // DONE: removed /id and /recipe

    // plugin enable/disable and the command operator
    @Override
    public void onEnable() {
        server = getServer();
        console = server.getConsoleSender();
        server.getPluginManager().registerEvents(this, this);
        loadTheTemporaryData();
        // done enabling
        String enable_message = enable_messages[(int) (Math.random() * enable_messages.length)];
        console.sendMessage(COLOR + enable_message);
        for (Player player : server.getOnlinePlayers())
            if (player.isOp())
                player.sendMessage(COLOR + enable_message);
    }

    @Override
    public void onDisable() {
        saveTheTemporaryData();
        // done disabling
        String disable_message = disable_messages[(int) (Math.random() * disable_messages.length)];
        console.sendMessage(COLOR + disable_message);
        for (Player player : server.getOnlinePlayers())
            if (player.isOp())
                player.sendMessage(COLOR + disable_message);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String command, String[] my_parameters) {
        parameters = my_parameters;
        if (command.equalsIgnoreCase("enable") || command.equalsIgnoreCase("en")) {
            if (sender instanceof Player && !sender.isOp())
                sender.sendMessage(ChatColor.RED + "Sorry, but you don't have permission to enable and disable plugins.");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You forgot to tell me which plugin to enable!");
            else {
                for (Plugin target : server.getPluginManager().getPlugins())
                    if (target.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
                        if (!target.isEnabled()) {
                            server.getPluginManager().enablePlugin(target);
                            sender.sendMessage(COLOR + target.getName() + " has been enabled.");
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
                            sender.sendMessage(COLOR + target.getName() + " has been disabled.");
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
                sender.sendMessage(COLOR + "" + ChatColor.ITALIC + "Noooooooooooo!!!\nWhy did it have to end like this?!");
                suicidal_maniacs.add(sender.getName());
            } else {
                for (Player target : server.getOnlinePlayers())
                    if (target.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
                        target.setHealth(0);
                        if (sender instanceof Player)
                            target.sendMessage(COLOR + sender.getName() + " killed you.");
                        else
                            target.sendMessage(COLOR + "Someone killed you.");
                        return true;
                    }
                sender.sendMessage(ChatColor.RED + "There's no one online named \"" + parameters[0] + ".\"");
            }
            return true;
        } else if (command.equalsIgnoreCase("gamemode") || command.equalsIgnoreCase("gm")) {
            if (!(sender instanceof Player) || sender.isOp())
                changeGameMode(sender);
            else
                sender.sendMessage(ChatColor.RED + "Sorry, but you're not allowed to change gamemodes.");
            return true;
        } else if (command.equalsIgnoreCase("position") || command.equalsIgnoreCase("pos")) {
            if (sender instanceof Player)
                sender.sendMessage("You are at (" + ((Player) sender).getLocation().getBlockX() + ", " + ((Player) sender).getLocation().getBlockY() + ", "
                        + ((Player) sender).getLocation().getBlockZ() + ") facing (" + ((Player) sender).getLocation().getPitch() + "[=pitch], "
                        + ((Player) sender).getLocation().getYaw() + "[=yaw]).");
            else
                sender.sendMessage(ChatColor.RED + "You're a console.... You have no position!");
            return true;
        } else if (command.equalsIgnoreCase("password") || command.equalsIgnoreCase("pw")) {
            if (!(sender instanceof Player))
                sender.sendMessage(COLOR + "You cannot set a password. Consoles do not require passwords.");
            else if (parameters.length == 0)
                if (passwords.get(((Player) sender).getName()) != null && passwords.get(((Player) sender).getName()).length() > 1)
                    sender.sendMessage(ChatColor.RED + "You seem to have forgotten to give the new password.");
                else
                    sender.sendMessage(ChatColor.RED + "You seem to have forgotten to give the password.");
            else {
                String password = parameters[0];
                if (parameters.length > 1)
                    for (int i = 1; i < parameters.length; i++)
                        password += " " + parameters[i];
                password = password.trim();
                if (password.length() <= 1)
                    sender.sendMessage(ChatColor.RED + "The length of your password must exceed one character.");
                else if (passwords.get(((Player) sender).getName()) != null && passwords.get(((Player) sender).getName()).length() > 1) {
                    password_resetters.put(((Player) sender).getName(), password);
                    sender.sendMessage(COLOR + "Please type your old password into the chat so that I may confirm your identity.");
                } else {
                    passwords.put(((Player) sender).getName(), password);
                    sender.sendMessage(COLOR + "Thank you for your cooperation.\nFrom now on, your password for this server will be \"" + password
                            + "\".\nWhen you log into the server, just type the password into the chat to play.\nRemember that your password is case-sensitive.");
                }
            }
            return true;
        } else if (command.equalsIgnoreCase("load") || command.equalsIgnoreCase("l")) {
            if (sender instanceof Player && !sender.isOp())
                sender.sendMessage(ChatColor.RED + "You do not have permssion to load new plugins.");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You have not designated a plugin to load.");
            else
                loadPlugin(sender, parameters[0]);
            return true;
        } else if (command.equalsIgnoreCase("unload") || command.equalsIgnoreCase("ul")) {
            if (sender instanceof Player && !sender.isOp())
                sender.sendMessage(ChatColor.RED + "You do not have permssion to unload plugins from this server.");
            else if (parameters.length == 0)
                sender.sendMessage(ChatColor.RED + "You have not designated a plugin to unload.");
            else
                unloadPlugin(sender, parameters[0]);
            return true;
        }
        return false;
    }

    // intra-command methods
    public static void debug(String message) {
        if (debugging_players.size() == 0)
            return;
        if (debugging_players.contains("console")) {
            console.sendMessage(COLOR + message);
            if (debugging_players.size() == 1)
                return;
        }
        for (Player player : server.getOnlinePlayers())
            if (debugging_players.contains(player.getName()))
                player.sendMessage(COLOR + message);
    }

    // listeners
    @EventHandler
    public void reduceTheSpawnRateOfBabyZombies(CreatureSpawnEvent event) {
        if (event.getEntityType() == EntityType.ZOMBIE && ((Zombie) event.getEntity()).isBaby() && Math.random() <= 0.75) {
            debug("I cancelled the spawning of a baby zombie.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void askForPasswordAndKeepKickedPlayersFromLoggingBackOnEarly(PlayerJoinEvent event) {
        // TODO: change the password requirements so that they have to use /password, not type the password into the chat
        if (kick_timers.containsKey(event.getPlayer().getName()) && time.getTimeInMillis() > kick_timers.get(event.getPlayer().getName())) {
            event.setJoinMessage("");
            event.getPlayer().kickPlayer(
                    COLOR + "Sorry, but you're still not allowed back on this server for another "
                            + SU.writeTime((int) (time.getTimeInMillis() - kick_timers.get(event.getPlayer().getName())), true) + ".");
        } else if (relogging_players.contains(event.getPlayer().getName()))
            relogging_players.remove(event.getPlayer().getName());
        else if (passwords.get(event.getPlayer().getName()) == null) {
            passwords.put(event.getPlayer().getName(), "1");
            event.getPlayer().sendMessage(
                    COLOR + "This server uses a password system for protection against hackers and siblings who could take over your account.\nPlease use " + ChatColor.ITALIC
                            + "/password [password]" + COLOR
                            + " to set your password.\nThe only restriction is that it must be more than one character.\nPlease do not use your Minecraft password.");
        } else if (passwords.get(event.getPlayer().getName()).length() == 1) {
            byte previous_logins = Byte.parseByte(passwords.get(event.getPlayer().getName()));
            if (previous_logins == 1)
                event.getPlayer().sendMessage(
                        COLOR + "You haven't set your password yet. For the sake of this server and your stuff on it, please set your password now.\nYou can use "
                                + ChatColor.ITALIC + "/password [password]" + COLOR
                                + " to set your password.\nThe only restriction is that it must be more than one character.\nPlease do not use your Minecraft password.");
            else if (previous_logins == 2)
                event.getPlayer().sendMessage(COLOR + "Please set your password now.\nUse " + ChatColor.ITALIC + "/password [password]" + COLOR + " to set your password.");
            else {
                // here, the person being forced to set their password will be considered a "password inputter", but when they try to "input the password",
                // myOpAids can see that they have no set password and it will set their input as their new password instead of checking that it's correct
                password_inputters.put(event.getPlayer().getName(), 0);
                event.getPlayer().sendMessage(
                        COLOR + "You are required to set your password. You will be allowed to play after you use " + ChatColor.ITALIC + "/password [password] " + COLOR
                                + "to set your password.");
            }
            passwords.put(event.getPlayer().getName(), String.valueOf(previous_logins + 1));
        } else {
            password_inputters.put(event.getPlayer().getName(), 0);
            event.getPlayer().sendMessage(COLOR + "Please input your server password!");
        }
        // remind debugging admins that they're debugging
        if (debugging_players.contains(event.getPlayer().getName()))
            event.getPlayer().sendMessage(COLOR + "Your debugging messages are still on for myOpAids!");
        if (gamemodes_to_change.containsKey(event.getPlayer().getName())) {
            GameMode new_gamemode = gamemodes_to_change.get(event.getPlayer().getName());
            event.getPlayer().setGameMode(new_gamemode);
            if (new_gamemode == GameMode.CREATIVE)
                event.getPlayer().sendMessage(COLOR + "You are now in Creative Mode. Go nuts. Have fun.");
            else if (new_gamemode == GameMode.SURVIVAL)
                event.getPlayer().sendMessage(COLOR + "You are now in Survival Mode. Watch out for monsters.");
            else
                event.getPlayer().sendMessage(COLOR + "You are now in Adventure Mode. You may do as Finn and Jake do.");
            gamemodes_to_change.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void stopPassWordInputtersFromMoving(PlayerMoveEvent event) {
        if (password_inputters.containsKey(event.getPlayer().getName())
                && !(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() <= event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())) {
            Location cancel_to = event.getFrom();
            // allow looking
            cancel_to.setPitch(event.getTo().getPitch());
            cancel_to.setYaw(event.getTo().getYaw());
            // allow falling
            if (event.getTo().getY() < event.getFrom().getY())
                cancel_to.setY(event.getTo().getY());
            event.getPlayer().teleport(event.getFrom());
            event.getPlayer().sendMessage(COLOR + "You have to put in your server password before you can move.");
        }
    }

    @EventHandler
    public void stopPassWordInputtersFromInteracting(PlayerInteractEvent event) {
        if (password_inputters.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(COLOR + "You have to put in your server password before you can build or use things.");
        }
    }

    @EventHandler
    public void stopPassWordInputtersFromInteractingWithEntities(PlayerInteractEntityEvent event) {
        if (password_inputters.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(COLOR + "You have to put in your server password before you can build or use things.");
        }
    }

    @EventHandler
    public void stopPassWordInputtersFromUsingCommands(PlayerCommandPreprocessEvent event) {
        if (password_inputters.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(COLOR + "You have to put in your server password before you can use commands.");
        }
    }

    @EventHandler
    public void stopPassWordInputtersFromMovingInVehicles(VehicleMoveEvent event) {
        if (event.getVehicle().getPassenger() instanceof Player && password_inputters.containsKey(((Player) event.getVehicle().getPassenger()).getName())
                && !(event.getTo().getX() == event.getFrom().getX() && event.getTo().getY() == event.getFrom().getY() && event.getTo().getZ() == event.getFrom().getZ())) {
            event.getVehicle().setVelocity(new Vector(0, 0, 0));
            event.getVehicle().teleport(event.getFrom());
            ((Player) (event.getVehicle().getPassenger())).sendMessage(COLOR + "You have to put in your server password before you can move.");
        }
    }

    @EventHandler
    public void rejoiceAtThePlayersRespawnAfterSuicide(PlayerRespawnEvent event) {
        if (suicidal_maniacs.contains(event.getPlayer().getName())) {
            suicidal_maniacs.remove(event.getPlayer().getName());
            event.getPlayer().sendMessage(
                    COLOR + "..." + event.getPlayer().getName().substring(0, 1).toUpperCase() + event.getPlayer().getName().substring(1) + "? Is that really you?\n"
                            + ChatColor.ITALIC + "You're not dead! Yaaaaaaaaaay!!!!!!");
        }
    }

    @EventHandler
    public void recordThePlayersGameModeBeforeTheyLogOff(PlayerQuitEvent event) {
        offline_player_gamemodes.put(event.getPlayer().getName(), event.getPlayer().getGameMode());
        relogging_players.add(event.getPlayer().getName());
        // 15 seconds = 300 ticks
        server.getScheduler().scheduleSyncDelayedTask(this, new myOpAids$1(event.getPlayer(), "track relogging"), 300);
    }

    @EventHandler
    public void readPasswordInputs(AsyncPlayerChatEvent event) {
        if (password_inputters.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            if (passwords.get(event.getPlayer().getName()) == null || passwords.get(event.getPlayer().getName()).length() == 1) {
                if (event.getMessage().trim().length() <= 1)
                    event.getPlayer().sendMessage(COLOR + "The length of your password must exceed one character.");
                else {
                    passwords.put(event.getPlayer().getName(), event.getMessage().trim());
                    password_inputters.remove(event.getPlayer().getName());
                    event.getPlayer().sendMessage(
                            COLOR + "Thank you for your cooperation.\nFrom now on, your password for this server will be \"" + event.getMessage().trim()
                                    + "\".\nRemember your password and remember that it is case-sensitive.");
                }
            } else if (event.getMessage().equals(passwords.get(event.getPlayer().getName()))) {
                password_inputters.remove(event.getPlayer().getName());
                event.getPlayer().sendMessage(COLOR + "Welcome to the server!");
            } else if (password_inputters.get(event.getPlayer().getName()) % number_of_attempts == 0 && password_inputters.get(event.getPlayer().getName()) > 0) {
                // players are kicked from the server for 2^(the number of times they've been kicked for failing the password input) minutes
                event.getPlayer().kickPlayer(
                        COLOR + "Sorry, but you're out of attempts. You can come on and try again in "
                                + Math.pow(2, password_inputters.get(event.getPlayer().getName()) / number_of_attempts * 2) + " minutes");
                kick_timers.put(event.getPlayer().getName(), time.getTimeInMillis()
                        + (long) (Math.pow(2, password_inputters.get(event.getPlayer().getName()) / number_of_attempts * 2) * 60000));
            } else {
                password_inputters.put(event.getPlayer().getName(), password_inputters.get(event.getPlayer().getName()) + 1);
                event.getPlayer().sendMessage(
                        COLOR + "That password is incorrect. You have " + (number_of_attempts - password_inputters.get(event.getPlayer().getName())) + " tries left.");
            }
        } else if (password_resetters.containsKey(event.getPlayer().getName())) {
            event.setCancelled(true);
            if (passwords.get(event.getPlayer().getName()).equals(event.getMessage())) {
                passwords.put(event.getPlayer().getName(), password_resetters.get(event.getPlayer().getName()));
                event.getPlayer().sendMessage(COLOR + "You have successfully changed your password to \"" + password_resetters.get(event.getPlayer().getName()) + "\".");
            } else
                event.getPlayer().sendMessage(COLOR + "That password is incorrect. Your password will not be reset.");
            password_resetters.remove(event.getPlayer().getName());
        }
    }

    @EventHandler
    public void protectPasswordInputtersFromMonsters(EntityTargetEvent event) {
        if (event.getTarget() instanceof Player && password_inputters.containsKey(((Player) event.getTarget()).getName()))
            event.setCancelled(true);
    }

    @EventHandler
    public void protectPasswordInputtersFromDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && password_inputters.containsKey(((Player) event.getEntity()).getName()) && event.getCause() != DamageCause.FALL)
            event.setCancelled(true);
    }

    // loading
    public void loadTheTemporaryData() {
        debug("Loading the temporary data...");
        passwords = new HashMap<String, String>();
        offline_player_gamemodes = new HashMap<String, GameMode>();
        gamemodes_to_change = new HashMap<String, GameMode>();
        File temp_file = new File(getDataFolder(), "temp.txt");
        if (!temp_file.exists())
            return;
        // read the temp.txt file
        try {
            if (!temp_file.setWritable(true))
                debug(ChatColor.DARK_RED + "I couldn't make the temporary data file writable for loading myOpAids!");
            BufferedReader in = new BufferedReader(new FileReader(temp_file));
            String save_line, parsing = "";
            while (true) {
                save_line = in.readLine();
                if (save_line == null)
                    break;
                save_line = save_line.trim();
                debug(ChatColor.WHITE + save_line);
                if (!save_line.equals(""))
                    if (save_line.startsWith("==== ") && save_line.endsWith(" ====")) {
                        parsing = save_line.substring(5, save_line.length() - 5).trim();
                        debug("Beginning parsing " + parsing + "...");
                    } else {
                        String user = save_line.split(": ")[0], remainder = save_line.substring(user.length() + 2);
                        if (parsing.equals("passwords")) {
                            passwords.put(user, remainder);
                            debug(user + "'s password: \"" + passwords.get(user) + "\"");
                        } else if (parsing.equals("offline player gamemodes")) {
                            offline_player_gamemodes.put(user, GameMode.getByValue(Integer.parseInt(remainder)));
                            debug(user + " was in " + offline_player_gamemodes.get(user).name().substring(0, 1)
                                    + offline_player_gamemodes.get(user).name().substring(1).toLowerCase() + " Mode at the end of their last session on the server.");
                        } else if (parsing.equals("gamemodes to change")) {
                            gamemodes_to_change.put(user, GameMode.getByValue(Integer.parseInt(remainder)));
                            debug(user + " will be put in " + gamemodes_to_change.get(user).name().substring(0, 1)
                                    + gamemodes_to_change.get(user).name().substring(1).toLowerCase() + " Mode the next time they enter the server.");
                        } else
                            debug(ChatColor.RED + "I am unsure what to do with this line!");
                    }
            }
            in.close();
            if (!temp_file.delete())
                debug(ChatColor.DARK_RED + "I could not delete the temporary data file for myOpAids after loading its contents!");
        } catch (IOException e) {
            MU.err(this, "I got an IOException while trying to save your temporary data for myOpAids!", e);
            return;
        }
        saveTheTemporaryData();
    }

    // saving
    public void saveTheTemporaryData() {
        File temp_file = new File(getDataFolder(), "temp.txt");
        try {
            // check the temporary file
            if (!temp_file.exists()) {
                getDataFolder().mkdir();
                temp_file.createNewFile();
            }
            temp_file.setWritable(true);
            // save the temporary data
            BufferedWriter out = new BufferedWriter(new FileWriter(temp_file));
            out.write("==== passwords ====");
            out.newLine();
            for (String key : passwords.keySet()) {
                out.write(key + ": " + passwords.get(key));
                out.newLine();
            }
            out.write("==== offline player gamemodes ====");
            out.newLine();
            for (String key : offline_player_gamemodes.keySet()) {
                out.write(key + ": " + offline_player_gamemodes.get(key).getValue());
                out.newLine();
            }
            out.write("==== gamemodes to change ====");
            out.newLine();
            for (String key : gamemodes_to_change.keySet()) {
                out.write(key + ": " + gamemodes_to_change.get(key).getValue());
                out.newLine();
            }
            out.close();
            temp_file.setReadOnly();
        } catch (IOException e) {
            MU.err(this, "I got an IOException while trying to save your temporary data for myOpAids.", e);
            return;
        }
    }

    // plugin commands
    private void changeGameMode(CommandSender sender) {
        if (parameters.length == 0)
            if (sender instanceof Player)
                if (((Player) sender).getGameMode() == GameMode.CREATIVE) {
                    ((Player) sender).setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(COLOR + "You're now in Survival Mode. Watch out for monsters.");
                    MU.tellOps(COLOR + sender.getName() + " changed to Survival Mode.", true, ((Player) sender).getName());
                } else {
                    ((Player) sender).setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(COLOR + "You're now in Creative Mode. Go nuts. Have fun.");
                    MU.tellOps(COLOR + sender.getName() + " changed to Creative Mode.", true, ((Player) sender).getName());
                }
            else
                sender.sendMessage(ChatColor.RED + "You forgot to tell me whose gamemode you want me to change! I can't exactly change yours, can I?");
        else if (parameters.length == 1)
            if (sender instanceof Player && ("creative".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("1"))) {
                if (((Player) sender).getGameMode() != GameMode.CREATIVE) {
                    ((Player) sender).setGameMode(GameMode.CREATIVE);
                    sender.sendMessage(COLOR + "You're now in Creative Mode. Go nuts. Have fun.");
                    MU.tellOps(COLOR + sender.getName() + " changed to Creative Mode.", true, ((Player) sender).getName());
                } else
                    sender.sendMessage(ChatColor.RED + "You're already in Creative Mode!");
            } else if (sender instanceof Player && ("survival".startsWith(parameters[0].toLowerCase()) || parameters[0].equals("0"))) {
                if (!((Player) sender).getGameMode().equals(GameMode.SURVIVAL)) {
                    ((Player) sender).setGameMode(GameMode.SURVIVAL);
                    sender.sendMessage(COLOR + "You're now in Survival Mode. Watch out for monsters.");
                    MU.tellOps(COLOR + sender.getName() + " changed to Survival Mode.", true, ((Player) sender).getName());
                } else
                    sender.sendMessage(ChatColor.RED + "You're already in Survival Mode!");
            } else {
                for (Player player : server.getOnlinePlayers())
                    if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
                        if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                            player.setGameMode(GameMode.CREATIVE);
                            if (sender instanceof Player) {
                                player.sendMessage(COLOR + sender.getName() + " put you in Creative Mode.");
                                sender.sendMessage(COLOR + "You put " + player.getName() + " in Creative Mode.");
                                console.sendMessage(COLOR + sender.getName() + " put " + player.getName() + " in Creative Mode.");
                            } else {
                                player.sendMessage(COLOR + "Someone put you in Creative Mode from the console.");
                                console.sendMessage(COLOR + "You put " + player.getName() + " in Creative Mode.");
                            }
                        } else {
                            player.setGameMode(GameMode.SURVIVAL);
                            if (sender instanceof Player) {
                                player.sendMessage(COLOR + sender.getName() + " put you in Survival Mode.");
                                sender.sendMessage(COLOR + "You put " + player.getName() + " in Survival Mode.");
                                console.sendMessage(COLOR + sender.getName() + " put " + player.getName() + " in Survival Mode.");
                            } else {
                                player.sendMessage(COLOR + "Someone put you in Survival Mode from the console.");
                                console.sendMessage(COLOR + "You put " + player.getName() + " in Survival Mode.");
                            }
                        }
                        return;
                    }
                for (OfflinePlayer player : server.getOfflinePlayers())
                    if (player.getName().toLowerCase().startsWith(parameters[0].toLowerCase())) {
                        if (offline_player_gamemodes.get(player.getName()) == null)
                            sender.sendMessage(ChatColor.RED
                                    + "Sorry, but I don't remember what "
                                    + player.getName()
                                    + "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
                        else if (offline_player_gamemodes.get(player.getName()).equals(GameMode.SURVIVAL)) {
                            gamemodes_to_change.put(player.getName(), GameMode.CREATIVE);
                            sender.sendMessage(COLOR + "I'll put " + player.getName() + " in Creative Mode when they get back online.");
                        } else {
                            gamemodes_to_change.put(player.getName(), GameMode.SURVIVAL);
                            sender.sendMessage(COLOR + "I'll put " + player.getName() + " in Survival Mode when they get back online.");
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
                        online_player.sendMessage(COLOR + sender.getName() + " put you in " + new_gamemode.toString().substring(0, 1)
                                + new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
                        sender.sendMessage(COLOR + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
                                + new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
                        console.sendMessage(COLOR + sender.getName() + " put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
                                + new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
                    } else {
                        online_player.sendMessage(COLOR + "Someone put you in " + new_gamemode.toString().substring(0, 1) + new_gamemode.toString().substring(1).toLowerCase()
                                + " Mode from the console.");
                        console.sendMessage(COLOR + "You put " + online_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
                                + new_gamemode.toString().substring(1).toLowerCase() + " Mode.");
                    }
                    return;
                }
            for (OfflinePlayer offline_player : server.getOfflinePlayers())
                if (offline_player.getName().toLowerCase().startsWith(player)) {
                    if (new_gamemode != null) {
                        gamemodes_to_change.put(offline_player.getName(), new_gamemode);
                        sender.sendMessage(COLOR + "I'll put " + offline_player.getName() + " in " + new_gamemode.toString().substring(0, 1)
                                + new_gamemode.toString().substring(1).toLowerCase() + " Mode when they get back online.");
                    } else if (offline_player_gamemodes.get(offline_player.getName()) == null)
                        sender.sendMessage(ChatColor.RED
                                + "Sorry, but I don't remember what "
                                + offline_player.getName()
                                + "'s gamemode was before they logged off, so I'm not sure what to change their gamemode to. Can you please use two parameters and confirm what gamemode you want me to change them to when they come back on?");
                    else if (offline_player_gamemodes.get(offline_player.getName()).equals(GameMode.SURVIVAL)) {
                        gamemodes_to_change.put(offline_player.getName(), GameMode.CREATIVE);
                        sender.sendMessage(COLOR + "I'll put " + offline_player.getName() + " in Creative Mode when they get back online.");
                    } else {
                        gamemodes_to_change.put(offline_player.getName(), GameMode.SURVIVAL);
                        sender.sendMessage(COLOR + "I'll put " + offline_player.getName() + " in Survival Mode when they get back online.");
                    }
                    return;
                }
            sender.sendMessage(ChatColor.RED + "Sorry, but I don't believe anyone named \"" + parameters[0] + "\" has ever played on this server.");
        }
    }

    private void loadPlugin(CommandSender sender, String name) {
        // find the plugin file specified
        File plugin_file = null;
        for (File file : getDataFolder().getParentFile().listFiles())
            if (file.getName().endsWith(".jar") && file.getName().toLowerCase().startsWith(name.toLowerCase())) {
                plugin_file = file;
                break;
            }
        if (plugin_file == null) {
            sender.sendMessage(ChatColor.RED + "I was unable to locate a plugin file called \"" + name + "\".");
            return;
        }

        // make sure the plugin isn't already loaded
        for (Plugin plugin : server.getPluginManager().getPlugins())
            if (plugin_file.getName().toLowerCase().startsWith(plugin.getName().toLowerCase())) {
                sender.sendMessage(ChatColor.RED + plugin.getName() + " is already loaded on your server.");
                return;
            }

        try {
            // attempt to load the plugin
            Plugin plugin = server.getPluginManager().loadPlugin(plugin_file);
            if (plugin == null) {
                sender.sendMessage(ChatColor.RED + "For some unknown reason, I was unable to load " + name + ".");
                return;
            }

            // enable the plugin and finish
            server.getPluginManager().enablePlugin(plugin);
            sender.sendMessage(COLOR + plugin.getName() + " v" + plugin.getDescription().getVersion() + " has been successfully loaded.");
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + plugin_file.getName().substring(0, plugin_file.getName().length() - 4) + " has a dependency that has not been satisfied.");
            return;
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void unloadPlugin(CommandSender sender, String name) {
        SimplePluginManager manager = (SimplePluginManager) server.getPluginManager();

        // find the target plugin
        Plugin plugin = null;
        for (Plugin _plugin : manager.getPlugins())
            if (_plugin.getName().toLowerCase().startsWith(name.toLowerCase())) {
                debug("plugin unload target found: " + _plugin.getName());
                plugin = _plugin;
                break;
            }
        if (plugin == null) {
            sender.sendMessage(ChatColor.RED + "I could not find a \"" + name + "\" plugin running on your server.");
            return;
        }

        // unload the plugin (Technius's code)
        Map ln;
        List<Plugin> pl;
        try {
            Field lnF = manager.getClass().getDeclaredField("lookupNames");
            lnF.setAccessible(true);
            ln = (Map) lnF.get(manager);

            Field plF = manager.getClass().getDeclaredField("plugins");
            plF.setAccessible(true);
            pl = (List<Plugin>) plF.get(manager);
        } catch (Exception e) {
            MU.err(this, "There was an issue trying to remove " + plugin.getName() + " from the server's fields.", e);
            return;
        }
        manager.disablePlugin(plugin);
        synchronized (manager) {
            ln.remove(plugin.getName());
            pl.remove(plugin);
        }

        SimpleCommandMap cmd_map;
        Map<String, Command> known_commands;
        try {
            // get the command map
            Field scmF = manager.getClass().getDeclaredField("commandMap");
            scmF.setAccessible(true);
            cmd_map = ((SimpleCommandMap) scmF.get(manager));

            // get the known commands
            Field kcF = cmd_map.getClass().getDeclaredField("knownCommands");
            kcF.setAccessible(true);
            known_commands = ((Map<String, Command>) kcF.get(cmd_map));
        } catch (Exception e) {
            MU.err(this, "There was an issue trying to remove " + plugin.getName() + "'s commands from the server's fields.", e);
            return;
        }

        synchronized (cmd_map) {
            Iterator<Map.Entry<String, Command>> it = known_commands.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Command> entry = (Map.Entry<String, Command>) it.next();
                if ((entry.getValue() instanceof PluginCommand)) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin().getName().equalsIgnoreCase(plugin.getName())) {
                        c.unregister(cmd_map);
                        it.remove();
                    }
                }
            }
        }
        JavaPluginLoader jpl = (JavaPluginLoader) plugin.getPluginLoader();
        Field loadersF;
        try {
            loadersF = jpl.getClass().getDeclaredField("loaders0");
            loadersF.setAccessible(true);
            Map loaderMap = (Map) loadersF.get(jpl);
            loaderMap.remove(plugin.getDescription().getName());
        } catch (Exception e) {
            MU.err(this, "There was an issue trying to remove " + plugin.getName() + " from the server's JavaPluginLoader.", e);
            return;
        }

        // close the class loader
        ClassLoader cl = plugin.getClass().getClassLoader();
        if (cl instanceof PluginClassLoader) {
            PluginClassLoader pcl = (PluginClassLoader) cl;
            try {
                Method m = pcl.getClass().getMethod("close", new Class[0]);
                m.setAccessible(true);
                m.invoke(pcl, new Object[0]);
            } catch (Exception e) {
                try {
                    pcl.close();
                } catch (IOException e2) {
                    MU.err(this, "I got an IOException trying to close this PluginClassLoader!", e2);
                }
                return;
            }
            try {
                pcl.close();
            } catch (IOException e) {
                MU.err(this, "I got an IOException trying to close this PluginClassLoader! (2)", e);
            }
        }

        // take out the trash
        System.gc();
        System.gc();

        // send the successful confirmation message
        sender.sendMessage(COLOR + plugin.getName() + " v" + plugin.getDescription().getVersion() + " has been unloaded.");
    }
}
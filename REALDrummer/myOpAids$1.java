package REALDrummer;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class myOpAids$1 implements Runnable {

    private CommandSender sender;
    private String method;
    @SuppressWarnings("unused")
    private Object[] objects;

    public myOpAids$1(CommandSender my_sender, String my_method, Object... my_objects) {
        sender = my_sender;
        method = my_method;
        objects = my_objects;
    }

    // run() doesn't actually perform any real action; it's an operator, by which I mean it takes the input and directs the processor to the appropriate method
    public void run() {
        myOpAids.debug("myOpAids executed \"" + method + "\".");
        if (method.equals("track relogging")) {
            if (myOpAids.relogging_players.contains(sender.getName()))
                myOpAids.relogging_players.remove(sender.getName());
        } else
            sender.sendMessage(ChatColor.DARK_RED + "What the hell is \"" + method + "\"? Recheck your method input for this myOpAids thread.");
    }
}

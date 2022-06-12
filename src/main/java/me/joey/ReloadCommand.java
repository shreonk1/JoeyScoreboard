package me.joey;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

    JoeyScoreboard main;
    public ReloadCommand(JoeyScoreboard main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("joeyscoreboard")) {
            if(args.length < 1) {
                sender.sendMessage(Utils.color("&cPlease enter an argument."));
                return false;
            }
            if(args[0].equalsIgnoreCase("reload")) {
                long timeBefore = System.currentTimeMillis();
                main.loadConfigValues();

                long timeAfter = System.currentTimeMillis();
                long timeTaken = timeAfter - timeBefore;
                sender.sendMessage(Utils.color("&aReloaded Scoreboard. Took: " + timeTaken + "ms."));
            }
        }
        return false;
    }
}

package me.joey;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import me.joey.scoreboard.ScoreboardClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.logging.Logger;

public final class JoeyScoreboard extends JavaPlugin {

    private static final Logger log = Logger.getLogger("Minecraft");
    public static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;
    String scoreboardTitle;
    String scoreboardLine1;
    String scoreboardLine2;
    String scoreboardLine3;
    String scoreboardLine4;
    String scoreboardLine5;
    String scoreboardLine6;

    @Override
    public void onEnable() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "JoeyScoreboard" + ChatColor.GRAY + "] " + ChatColor.GOLD + "is now enabled!");
        getServer().getPluginManager().registerEvents(new ScoreboardClass(this), this);
        getCommand("joeyscoreboard").setExecutor(new ReloadCommand(this));
        if (!setupEconomy() ) {
            log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        saveDefaultConfig();
        loadConfigValues();
        setupPermissions();
        setupChat();
        updateScoreboards();
    }

    public void loadConfigValues() {
        reloadConfig();
        this.scoreboardTitle = getConfig().getString("scoreboardtitle");
        this.scoreboardLine1 = getConfig().getString("scoreboardline1");
        this.scoreboardLine2 = getConfig().getString("scoreboardline2");
        this.scoreboardLine3 = getConfig().getString("scoreboardline3");
        this.scoreboardLine4 = getConfig().getString("scoreboardline4");
        this.scoreboardLine5 = getConfig().getString("scoreboardline5");
        this.scoreboardLine6 = getConfig().getString("scoreboardline6");
    }


    public void setupScoreboard(Player player) {

        ScoreboardManager manager = Bukkit.getScoreboardManager();
        org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
        Objective obj = board.registerNewObjective("ScoreBoard ", "heheheheh", Utils.color(scoreboardTitle));
        Score score = obj.getScore(Utils.color(scoreboardLine1.replace("%player%", player.getName())));
        Score score2 = obj.getScore(Utils.color(scoreboardLine2.replace("%balance%", "" + JoeyScoreboard.getEconomy().getBalance(player))));
        Score score3 = obj.getScore(Utils.color(scoreboardLine3));
        Score score4 = obj.getScore(Utils.color(scoreboardLine4));
        Score score5 = obj.getScore(Utils.color(scoreboardLine5));
        Score score6 = obj.getScore(Utils.color(scoreboardLine6.replace("%motd%", Bukkit.getMotd())));
        score.setScore(5);
        score2.setScore(4);
        score3.setScore(3);
        score4.setScore(2);
        score5.setScore(1);
        score6.setScore(0);

        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        player.setScoreboard(board);
    }

    public void updateScoreboards() {
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(Player player : Bukkit.getOnlinePlayers()) {
                    setupScoreboard(player);
                }
            }
        }, 40, 40);
    }





























    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }

    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)) {
            log.info("Only players are supported for this Example Plugin, but you should not do this!!!");
            return true;
        }

        Player player = (Player) sender;

        if(command.getLabel().equals("test-economy")) {
            // Lets give the player 1.05 currency (note that SOME economic plugins require rounding!)
            sender.sendMessage(String.format("You have %s", econ.format(econ.getBalance(player.getName()))));
            EconomyResponse r = econ.depositPlayer(player, 1.05);
            if(r.transactionSuccess()) {
                sender.sendMessage(String.format("You were given %s and now have %s", econ.format(r.amount), econ.format(r.balance)));
            } else {
                sender.sendMessage(String.format("An error occured: %s", r.errorMessage));
            }
            return true;
        } else if(command.getLabel().equals("test-permission")) {
            // Lets test if user has the node "example.plugin.awesome" to determine if they are awesome or just suck
            if(perms.has(player, "example.plugin.awesome")) {
                sender.sendMessage("You are awesome!");
            } else {
                sender.sendMessage("You suck!");
            }
            return true;
        } else {
            return false;
        }
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static Permission getPermissions() {
        return perms;
    }

    public static Chat getChat() {
        return chat;
    }
}
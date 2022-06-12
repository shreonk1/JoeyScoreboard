package me.joey.scoreboard;

import me.joey.JoeyScoreboard;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ScoreboardClass implements Listener {

    JoeyScoreboard main;
    public ScoreboardClass(JoeyScoreboard main) {
        this.main = main;

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        main.setupScoreboard(e.getPlayer());
    }
}

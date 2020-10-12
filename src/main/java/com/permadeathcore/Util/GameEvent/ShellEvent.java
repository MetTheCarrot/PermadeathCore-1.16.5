package com.permadeathcore.Util.GameEvent;

import com.permadeathcore.Main;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class ShellEvent {

    private Main instance;
    private boolean running;

    private int timeLeft;
    private BossBar bossBar;
    private String title;

    public ShellEvent(Main instance) {
        this.instance = instance;
        this.timeLeft = 60*60*4;
        this.title = instance.format("&c&lX2 Shulker Shells: &b&n");
        this.bossBar = Bukkit.createBossBar(title, BarColor.RED, BarStyle.SOLID);
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public void addPlayer(Player p) {
        bossBar.addPlayer(p);
    }

    public void clearPlayers() {

        for (Player p : bossBar.getPlayers()) {

            bossBar.removePlayer(p);
        }
    }

    public void setTitle(String title) {
        this.title = title;
        this.bossBar.setTitle(title);
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(int timeLeft) {
        this.timeLeft = timeLeft;
    }

    public void reduceTime() {
        this.timeLeft = timeLeft - 1;
    }

    public void removePlayer(Player player) {

        if (bossBar.getPlayers().contains(player)) return;

        bossBar.addPlayer(player);
    }
}

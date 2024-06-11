package com.kzics.me.tasks;

import com.kzics.me.MultiEvents;
import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.utils.ColorsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;


public class EventsTask extends BukkitRunnable {

    private final EventsManager eventsManager;
    private final ManagerHandler managerHandler;
    public EventsTask(ManagerHandler managerHandler) {
        this.eventsManager = managerHandler.getEventsManager();
        this.managerHandler = managerHandler;
    }
    @Override
    public void run() {
        final GameEvent<?> activeEvent = eventsManager.getActiveEvent();
        if (activeEvent == null || !activeEvent.isActive()) return;

        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - activeEvent.getStartTime();
        long remainingTime = activeEvent.getDuration() * 60 * 1000L - elapsedTime;

        for (UUID player : activeEvent.getScores().keySet()) {
            Player p = Bukkit.getPlayer(player);
            if (p == null) continue;

            p.sendActionBar(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.actionbar-message")
                    .replace("%points%", String.valueOf(activeEvent.getScores().get(player)))
                    .replace("%position%", String.valueOf(activeEvent.getScorePosition(player)))
                    .replace("%time%", formatTime(remainingTime))));
        }

        if (remainingTime <= 0) {
            activeEvent.endEvent(MultiEvents.getPlugin().getManagerHandler());
            eventsManager.setActiveEvent(null);

        } else if (remainingTime == 5000) {
            Bukkit.broadcastMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.end-countdown-5")));
        } else if (remainingTime == 15000) {
            Bukkit.broadcastMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.end-countdown-5")));
        } else if (remainingTime == 30000) {
            Bukkit.broadcastMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.end-countdown-5")));
        }
    }
    private String formatTime(long timeInMillis) {
        long totalSeconds = timeInMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}


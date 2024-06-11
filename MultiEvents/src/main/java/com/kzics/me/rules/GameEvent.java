package com.kzics.me.rules;

import com.kzics.me.MultiEvents;
import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.utils.ColorsUtil;
import com.kzics.me.utils.DefaultFontInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.*;

public abstract class GameEvent<T extends Event> {

    private final String name;
    private Map<UUID, Integer> score;
    protected boolean isActive;
    private List<Location> spawnLocations;
    protected Long startTime;
    protected int duration;
    public GameEvent(String name, List<Location> spawnLocations){
        this.name = name;
        this.score = new HashMap<>();
        this.isActive = false;
        this.spawnLocations = spawnLocations;
    }

    public int getDuration() {
        return duration;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void startEvent(int duration, EventsManager eventsManager){
        onStart(duration);
        eventsManager.setActiveEvent(this);
    }

    public void endEvent(ManagerHandler manager) {
        onEnd();
        isActive = false;
        announceTop();
        this.startTime = null;
        this.score = new HashMap<>();
        FileConfiguration fileConfig = manager.getMain().getConfig();

        List<Map.Entry<UUID, Integer>> scoreList = new ArrayList<>(score.entrySet());
        scoreList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        String top1Command = fileConfig.getString("top1-command");
        String top2Command = fileConfig.getString("top2-command");
        String top3Command = fileConfig.getString("top3-command");

        if (top1Command != null && !scoreList.isEmpty()) {
            Player top1Player = Bukkit.getPlayer(scoreList.get(0).getKey());
            if (top1Player != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), top1Command.replace("%player%", top1Player.getName()));
            }
        }

        if (top2Command != null && scoreList.size() > 1) {
            Player top2Player = Bukkit.getPlayer(scoreList.get(1).getKey());
            if (top2Player != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), top2Command.replace("%player%", top2Player.getName()));
            }
        }

        if (top3Command != null && scoreList.size() > 2) {
            Player top3Player = Bukkit.getPlayer(scoreList.get(2).getKey());
            if (top3Player != null) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), top3Command.replace("%player%", top3Player.getName()));
            }
        }
    }


    public UUID getParticipant(Player player){
        return score.containsKey(player.getUniqueId()) ? player.getUniqueId() : null;
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public void addParticipant(Player player){
        onPlayerJoin(player);

        score.put(player.getUniqueId(), 0);
    }
    public void removeParticipant(Player player){
        onPlayerLeave(player);

        score.remove(player.getUniqueId());
    }

    public void announceTop(){
        List<Map.Entry<UUID, Integer>> scoreList = new ArrayList<>(score.entrySet());

        scoreList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (Player player : Bukkit.getOnlinePlayers()) {
            DefaultFontInfo.sendCenteredMessage(player, ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig()
                    .getString("messages.top-header")));

            for (int i = 0; i < 3 && i < scoreList.size(); i++) {
                Map.Entry<UUID, Integer> entry = scoreList.get(i);
                Player topPlayer = Bukkit.getPlayer(entry.getKey());
                if (topPlayer != null) {
                    DefaultFontInfo.sendCenteredMessage(player, Component.text((i + 1) + ". " + topPlayer.getName() + ": " + entry.getValue()).color(NamedTextColor.GOLD));
                }
            }

            UUID playerUUID = player.getUniqueId();
            for (int i = 0; i < scoreList.size(); i++) {
                if (scoreList.get(i).getKey().equals(playerUUID)) {
                    DefaultFontInfo.sendCenteredMessage(player, ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig()
                            .getString("messages.top-footer")
                            .replace("%position%", String.valueOf(i + 1))));
                    break;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    protected abstract void onStart(int duration);

    protected abstract void onEnd();

    protected abstract void onPlayerJoin(Player player);

    protected abstract void onPlayerLeave(Player player);

    public abstract void onPlayerAction(Player player, T action);

    public Map<UUID, Integer> getScores(){
        return score;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getScorePosition(UUID playerUUID) {
        List<Map.Entry<UUID, Integer>> scoreList = new ArrayList<>(score.entrySet());
        scoreList.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (int i = 0; i < scoreList.size(); i++) {
            if (scoreList.get(i).getKey().equals(playerUUID)) {
                return i + 1;
            }
        }
        return -1;
    }
}
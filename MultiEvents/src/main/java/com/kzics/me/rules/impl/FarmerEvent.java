package com.kzics.me.rules.impl;

import com.kzics.me.MultiEvents;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.utils.ColorsUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.*;

public class FarmerEvent extends GameEvent<BlockBreakEvent> {
    private final Map<Material, Integer> cropPoints;

    public FarmerEvent(String name, List<Location> spawnLocations,HashMap<Material,Integer> cropPoints, int time) {
        super(name, spawnLocations);
        this.cropPoints = cropPoints;
        this.duration = time;
    }

    @Override
    protected void onStart(int duration) {

        for (Player player : Bukkit.getOnlinePlayers()){
            player.sendMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.event-start")
                    .replace("%event%", getName())));

            player.sendMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.event-message")
                    .replace("%time%", String.valueOf(duration))));
        }
        Bukkit.getScheduler().runTaskLater(MultiEvents.getPlugin(),()->{
            this.isActive = true;

            for (UUID player : getScores().keySet()){
                Player p = Bukkit.getPlayer(player);
                if(p == null) continue;

                p.teleport(getSpawnLocations().get(new Random().nextInt(getSpawnLocations().size())));
            }

            this.startTime = System.currentTimeMillis();
            Bukkit.broadcastMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.event-start")
                    .replace("%event%", getName())));

        },duration * 20L);
    }

    @Override
    protected void onEnd() {
        Bukkit.getScheduler().runTaskLater(MultiEvents.getPlugin(),()->{
            for (Player player : Bukkit.getOnlinePlayers()){
                player.teleport(player.getWorld().getSpawnLocation());
            }
        },60L);
    }

    @Override
    protected void onPlayerJoin(Player player) {
        player.sendMessage(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.joined-event")
        .replace("%event%", getName())));
    }

    @Override
    protected void onPlayerLeave(Player player) {
        removeParticipant(player);
        player.teleport(player.getWorld().getSpawnLocation());
    }

    @Override
    public void onPlayerAction(Player player, BlockBreakEvent action) {
        Block block = action.getBlock();
        Material blockType = block.getType();

        if (cropPoints.containsKey(blockType)) {
            if (block.getBlockData() instanceof Ageable ageable) {
                if (ageable.getAge() == ageable.getMaximumAge()) {
                    int points = cropPoints.get(blockType);
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - getStartTime();
                    long remainingTime = getDuration() * 60 * 1000L - elapsedTime;

                    getScores().put(player.getUniqueId(), getScores().get(player.getUniqueId()) + points);
                    player.sendActionBar(ColorsUtil.translate.apply(MultiEvents.getPlugin().getConfig().getString("messages.actionbar-message-add")
                            .replace("%points%", String.valueOf(getScores().get(player.getUniqueId())))
                            .replace("%added_points%",String.valueOf(points))
                            .replace("%position%", String.valueOf(getScorePosition(player.getUniqueId())))
                            .replace("%time%", formatTime(remainingTime))));
                }
            }
        }
    }
    private String formatTime(long timeInMillis) {
        long totalSeconds = timeInMillis / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}

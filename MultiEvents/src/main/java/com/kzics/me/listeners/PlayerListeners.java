package com.kzics.me.listeners;

import com.kzics.me.manager.EventsManager;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.rules.impl.FarmerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerListeners implements Listener {

    private EventsManager eventsManager;
    public PlayerListeners(final EventsManager eventsManager){
        this.eventsManager = eventsManager;

    }
    @EventHandler
    public void onBreak(BlockBreakEvent event){
        if(!eventsManager.containsParticipant(FarmerEvent.class, event.getPlayer())) return;

        final Player player = event.getPlayer();

        GameEvent<BlockBreakEvent> gameEvent = eventsManager.getEvent(FarmerEvent.class);
        if(!gameEvent.isActive()) return;

        gameEvent.onPlayerAction(player, event);
    }
}

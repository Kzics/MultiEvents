package com.kzics.me.manager;

import com.kzics.me.MultiEvents;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.rules.impl.FarmerEvent;
import com.kzics.me.utils.ConfigurationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashMap;
import java.util.Map;

public class EventsManager {


    private final Map<Class<? extends GameEvent<?>>, GameEvent<?>> eventRegistery;
    private GameEvent<?> activeEvent;

    private final ConfigurationManager configManager;
    public EventsManager(final ConfigurationManager configManager) {
        this.eventRegistery = new HashMap<>();
        this.configManager = configManager;
        //this.eventRegistery.put(FarmerEvent.class, new FarmerEvent("Farmer", null));
    }

    public <T extends GameEvent<?>> void registerEvent(Class<T> eventClass, String eventName) {
        T event = configManager.loadEventConfig(eventClass, eventName);
        if (event != null) {
            eventRegistery.put(eventClass, event);
        }
    }

    public void setActiveEvent(GameEvent<?> activeEvent) {
        this.activeEvent = activeEvent;
    }

    public GameEvent<?> getActiveEvent() {
        return activeEvent;
    }

    public void addEvent(Class<? extends GameEvent<?>> event, GameEvent<?> gameEvent) {
        eventRegistery.put(event, gameEvent);
    }

    public void removeEvent(Class<? extends GameEvent<?>> event) {
        eventRegistery.remove(event);
    }

    public void addParticipant(Class<? extends GameEvent<?>> event, Player player) {
        GameEvent<?> gameEvent = eventRegistery.get(event);
        if (gameEvent != null) {
            gameEvent.addParticipant(player);
        }
    }

    public boolean containsParticipant(Class<? extends GameEvent<?>> event, Player player) {
        GameEvent<?> gameEvent = eventRegistery.get(event);
        if (gameEvent != null) {
            return gameEvent.getParticipant(player) != null;
        }
        return false;
    }

    public <T extends Event> GameEvent<T> getEvent(Class<? extends GameEvent<T>> eventClass) {
        return (GameEvent<T>) eventRegistery.get(eventClass);
    }
    public <T extends Event> GameEvent<T> getEvent(String eventName) {
        for (GameEvent<?> gameEvent : eventRegistery.values()) {
            if (gameEvent.getName().equalsIgnoreCase(eventName)) {
                return (GameEvent<T>) gameEvent;
            }
        }
        return null;
    }



    public void endEvent(Class<? extends GameEvent<?>> event) {
        GameEvent<?> gameEvent = eventRegistery.get(event);
        if (gameEvent != null) {
            gameEvent.endEvent(MultiEvents.getPlugin().getManagerHandler());
        }
    }
}

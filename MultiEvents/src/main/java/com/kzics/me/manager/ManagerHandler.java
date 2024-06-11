package com.kzics.me.manager;

import com.kzics.me.MultiEvents;
import com.kzics.me.utils.ConfigurationManager;

public class ManagerHandler {

    private final MultiEvents main;
    private EventsManager eventsManager;
    public ManagerHandler(MultiEvents main){
        this.main = main;
        this.eventsManager = new EventsManager(new ConfigurationManager(main));
    }

    public EventsManager getEventsManager() {
        return eventsManager;
    }

    public MultiEvents getMain() {
        return main;
    }
}

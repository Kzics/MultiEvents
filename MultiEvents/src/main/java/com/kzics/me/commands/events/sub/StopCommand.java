package com.kzics.me.commands.events.sub;

import com.kzics.me.commands.ICommand;
import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.utils.ColorsUtil;
import org.bukkit.command.CommandSender;

public class StopCommand implements ICommand {

    private final EventsManager eventsManager;
    private final ManagerHandler managerHandler;
    public StopCommand(ManagerHandler managerHandler) {
        this.eventsManager = managerHandler.getEventsManager();
        this.managerHandler = managerHandler;
    }
    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "Stop the event";
    }

    @Override
    public String getPermission() {
        return "multievents.stop";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 1){
            sender.sendMessage("Usage: /events stop");
            return;
        }

        if(eventsManager.getActiveEvent() == null){
            sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.no-active-events")));
            return;
        }

        GameEvent<?> gameEvent = eventsManager.getActiveEvent();
        gameEvent.endEvent(managerHandler);
        eventsManager.setActiveEvent(null);
    }
}

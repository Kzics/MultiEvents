package com.kzics.me.commands.events.sub;

import com.kzics.me.commands.ICommand;
import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.utils.ColorsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class StartCommand implements ICommand {

    private final EventsManager eventsManager;
    private final ManagerHandler managerHandler;
    public StartCommand(final ManagerHandler managerHandler){
        this.eventsManager = managerHandler.getEventsManager();
        this.managerHandler = managerHandler;
    }

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "Starts the event";
    }

    @Override
    public String getPermission() {
        return "multievents.start";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 3) {
            sender.sendMessage("Usage: /events start <event> <length>");
            return;
        }

        String eventName = args[1];
        int length = Integer.parseInt(args[2]);

        GameEvent<?> event = eventsManager.getEvent(eventName);
        if(event == null){
            sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.event-not-found")));
            return;
        }

        if(eventsManager.getActiveEvent() != null){
            sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.already-active-event")));
            return;
        }

        event.startEvent(length, eventsManager);
        sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.started-event")
                .replace("%event%", event.getName())));
    }
}

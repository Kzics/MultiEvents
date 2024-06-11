package com.kzics.me.commands.events.sub;

import com.kzics.me.commands.ICommand;
import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.rules.GameEvent;
import com.kzics.me.utils.ColorsUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements ICommand {

    private final EventsManager eventsManager;
    private final ManagerHandler managerHandler;
    public JoinCommand(ManagerHandler managerHandler){
        this.eventsManager = managerHandler.getEventsManager();
        this.managerHandler = managerHandler;
    }
    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "Join an event";
    }

    @Override
    public String getPermission() {
        return "multievents.join";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player player)) return;

        if(args.length != 1){
            sender.sendMessage("Usage: /events join");
            return;
        }

        GameEvent<?> activeEvent = eventsManager.getActiveEvent();
        if(activeEvent == null){
            sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.no-active-events")));
            return;
        }

        if(activeEvent.isActive()){
            sender.sendMessage(ColorsUtil.translate.apply(managerHandler.getMain().getConfig().getString("messages.no-active-events")));
            return;
        }


        activeEvent.addParticipant(player);
    }
}

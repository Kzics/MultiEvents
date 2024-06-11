package com.kzics.me.commands;

import com.kzics.me.manager.EventsManager;
import com.kzics.me.manager.ManagerHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandBase implements CommandExecutor {
    protected final EventsManager eventsManager;
    public CommandBase(ManagerHandler managerHandler) {
        this.eventsManager = managerHandler.getEventsManager();
    }

    protected Map<String, ICommand> subCommands = new HashMap<>();

    public void registerSubCommand(String name, ICommand command) {
        subCommands.put(name.toLowerCase(), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            ICommand subCommand = subCommands.get(args[0].toLowerCase());
            if (subCommand != null) {
                if(!sender.hasPermission(subCommand.getPermission())){
                    sender.sendMessage(Component.text("You don't have permission to execute this command.").color(NamedTextColor.RED));
                    return true;
                }
                subCommand.execute(sender, args);
                return true;
            }
            //subCommands.get("help").execute(sender, args);
        }

        return false;
    }
}

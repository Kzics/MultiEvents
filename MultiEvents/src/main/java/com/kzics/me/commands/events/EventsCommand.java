package com.kzics.me.commands.events;

import com.kzics.me.commands.CommandBase;
import com.kzics.me.commands.events.sub.JoinCommand;
import com.kzics.me.commands.events.sub.StartCommand;
import com.kzics.me.commands.events.sub.StopCommand;
import com.kzics.me.manager.ManagerHandler;

public class EventsCommand extends CommandBase {
    public EventsCommand(ManagerHandler managerHandler) {
        super(managerHandler);

        registerSubCommand("join", new JoinCommand(managerHandler));
        registerSubCommand("start", new StartCommand(managerHandler));
        registerSubCommand("stop", new StopCommand(managerHandler));
    }
}

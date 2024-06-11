package com.kzics.me;

import com.kzics.me.manager.ManagerHandler;
import com.kzics.me.rules.impl.FarmerEvent;
import com.kzics.me.tasks.EventsTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class MultiEvents extends JavaPlugin {

    private static MultiEvents plugin;
    private ManagerHandler managerHandler;

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) getDataFolder().mkdir();
        managerHandler = new ManagerHandler(this);


        File eventsFolder = new File(getDataFolder(), "events");
        if(!eventsFolder.exists()) eventsFolder.mkdir();

        try {
            File farmerFile = new File(eventsFolder, "farmer.yml");

            if(!new File(getDataFolder(), "config.yml").exists()) copyStreamToFile(getResource("config.yml"), new File(getDataFolder(), "config.yml"));
            if(!farmerFile.exists()) copyStreamToFile(getResource("farmer.yml"), farmerFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        managerHandler.getEventsManager().registerEvent(FarmerEvent.class, "farmer");
        plugin = this;

        getServer().getPluginManager().registerEvents(new com.kzics.me.listeners.PlayerListeners(managerHandler.getEventsManager()), this);

        getCommand("events").setExecutor(new com.kzics.me.commands.events.EventsCommand(managerHandler));

        new EventsTask(managerHandler)
                .runTaskTimer(this, 0, 20);
    }

    public static MultiEvents getPlugin() {
        return plugin;
    }

    public ManagerHandler getManagerHandler() {
        return managerHandler;
    }

    public void copyStreamToFile(InputStream source, File destination) throws IOException {
        try (OutputStream out = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = source.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        } finally {
            if (source != null) {
                source.close();
            }
        }
    }
}

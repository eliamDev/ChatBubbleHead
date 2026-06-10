package com.diacero.chatbubbles;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class DCChatBubbles extends JavaPlugin {

    private ChatBubbleListener bubbleListener;
    private Set<UUID> hiddenPlayers = new HashSet<>();
    private File dataFile;
    private FileConfiguration dataConfig;
    private boolean globalEnabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadDataFile();

        bubbleListener = new ChatBubbleListener(this);
        getServer().getPluginManager().registerEvents(bubbleListener, this);
        getCommand("chatbubble").setExecutor(new ChatBubbleCommand(this));
        getCommand("chatbubble").setTabCompleter(new ChatBubbleTabCompleter(this));

        getLogger().info("DCChatBubbles v" + getDescription().getVersion() + " habilitado con Toggle Personal.");
    }

    @Override
    public void onDisable() {
        if (bubbleListener != null)
            bubbleListener.removeAll();
        saveDataFile();
        getLogger().info("DCChatBubbles deshabilitado.");
    }

    private void loadDataFile() {
        dataFile = new File(getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                getLogger().severe("No se pudo crear data.yml");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        List<String> uuids = dataConfig.getStringList("hidden_players");
        for (String u : uuids) {
            try {
                hiddenPlayers.add(UUID.fromString(u));
            } catch (Exception ignored) {
            }
        }
    }

    private void saveDataFile() {
        if (dataFile == null || dataConfig == null)
            return;
        List<String> uuids = hiddenPlayers.stream().map(UUID::toString).collect(Collectors.toList());
        dataConfig.set("hidden_players", uuids);
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            getLogger().severe("No se pudo guardar data.yml");
        }
    }

    /**
     * Alterna el estado de un jugador. Devuelve true si ahora sus globos están
     * OCULTOS.
     */
    public boolean togglePlayer(UUID uuid) {
        if (hiddenPlayers.contains(uuid)) {
            hiddenPlayers.remove(uuid);
            return false; // Ahora son visibles
        } else {
            hiddenPlayers.add(uuid);
            return true; // Ahora están ocultos
        }
    }

    public boolean isPlayerHidden(UUID uuid) {
        return hiddenPlayers.contains(uuid);
    }

    public Set<UUID> getHiddenPlayers() {
        return hiddenPlayers;
    }

    public boolean toggleGlobal() {
        globalEnabled = !globalEnabled;
        if (!globalEnabled && bubbleListener != null) {
            bubbleListener.removeAll();
        }
        return globalEnabled;
    }

    public boolean isGlobalEnabled() {
        return globalEnabled;
    }

    public ChatBubbleListener getBubbleListener() {
        return bubbleListener;
    }
}

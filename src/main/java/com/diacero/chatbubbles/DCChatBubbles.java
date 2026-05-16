package com.diacero.chatbubbles;

import org.bukkit.plugin.java.JavaPlugin;

public class DCChatBubbles extends JavaPlugin {

    private ChatBubbleListener bubbleListener;
    private boolean enabled = true;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        bubbleListener = new ChatBubbleListener(this);
        getServer().getPluginManager().registerEvents(bubbleListener, this);
        getCommand("chatbubble").setExecutor(new ChatBubbleCommand(this));
        getLogger().info("DCChatBubbles v" + getDescription().getVersion() + " habilitado.");
    }

    @Override
    public void onDisable() {
        if (bubbleListener != null) bubbleListener.removeAll();
        getLogger().info("DCChatBubbles deshabilitado.");
    }

    /** Alterna el estado. Devuelve el nuevo estado (true = activo). */
    public boolean toggleBubbles() {
        enabled = !enabled;
        if (!enabled && bubbleListener != null) bubbleListener.removeAll();
        return enabled;
    }

    public boolean isBubblesEnabled() {
        return enabled;
    }

    public ChatBubbleListener getBubbleListener() {
        return bubbleListener;
    }
}

package com.diacero.chatbubbles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ChatBubbleCommand implements CommandExecutor {

    private final DCChatBubbles plugin;

    public ChatBubbleCommand(DCChatBubbles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("dc.chatbubble")) {
            sender.sendMessage(Component.text("✗ No tienes permiso.").color(NamedTextColor.RED));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            plugin.getBubbleListener().reloadConfig();
            sender.sendMessage(Component.text("✓ DCChatBubbles: config recargado.").color(NamedTextColor.GREEN));
            return true;
        }

        boolean ahora = plugin.toggleBubbles();

        if (ahora) {
            plugin.getServer().broadcast(
                    Component.text("✓ Globos de chat ").color(NamedTextColor.GREEN)
                            .append(Component.text("activados").color(NamedTextColor.GREEN).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                            .append(Component.text(".").color(NamedTextColor.GREEN)));
        } else {
            plugin.getServer().broadcast(
                    Component.text("✗ Globos de chat ").color(NamedTextColor.RED)
                            .append(Component.text("desactivados").color(NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                            .append(Component.text(".").color(NamedTextColor.RED)));
        }

        return true;
    }
}

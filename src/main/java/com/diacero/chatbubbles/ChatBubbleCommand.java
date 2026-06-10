package com.diacero.chatbubbles;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatBubbleCommand implements CommandExecutor {

    private final DCChatBubbles plugin;

    public ChatBubbleCommand(DCChatBubbles plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Comando de recarga (Solo Admins)
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("dc.chatbubble.reload")) {
                sender.sendMessage(Component.text("✗ No tienes permiso para recargar la configuración.").color(NamedTextColor.RED));
                return true;
            }
            plugin.reloadConfig();
            plugin.getBubbleListener().reloadConfig();
            sender.sendMessage(Component.text("✓ DCChatBubbles: Configuración recargada exitosamente.").color(NamedTextColor.GREEN));
            return true;
        }

        // Interruptor Global (Solo Admins)
        if (args.length > 0 && args[0].equalsIgnoreCase("global")) {
            if (!sender.hasPermission("dc.chatbubble.global")) {
                sender.sendMessage(Component.text("✗ No tienes permiso para apagar las burbujas globales.").color(NamedTextColor.RED));
                return true;
            }
            boolean ahora = plugin.toggleGlobal();
            if (ahora) {
                plugin.getServer().broadcast(
                        Component.text("✓ Globos de chat ").color(NamedTextColor.GREEN)
                                .append(Component.text("activados globalmente").color(NamedTextColor.GREEN).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                                .append(Component.text(".").color(NamedTextColor.GREEN)));
            } else {
                plugin.getServer().broadcast(
                        Component.text("✗ Globos de chat ").color(NamedTextColor.RED)
                                .append(Component.text("desactivados globalmente").color(NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                                .append(Component.text(".").color(NamedTextColor.RED)));
            }
            return true;
        }

        // Toggle Personal (Cualquier jugador)
        if (!sender.hasPermission("dc.chatbubble.toggle")) {
            sender.sendMessage(Component.text("✗ No tienes permiso para usar este comando.").color(NamedTextColor.RED));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Component.text("Este comando solo puede ser usado por jugadores en el juego.").color(NamedTextColor.RED));
            return true;
        }

        Player player = (Player) sender;
        boolean ocultado = plugin.togglePlayer(player.getUniqueId());

        if (ocultado) {
            // Borra instantáneamente los globos que el jugador ya tuviera activos sobre su cabeza
            plugin.getBubbleListener().removeAllBubbles(player.getUniqueId());
            
            player.sendMessage(
                    Component.text("✗ Globos de chat ").color(NamedTextColor.RED)
                            .append(Component.text("desactivados").color(NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                            .append(Component.text(" para ti.").color(NamedTextColor.RED)));
        } else {
            player.sendMessage(
                    Component.text("✓ Globos de chat ").color(NamedTextColor.GREEN)
                            .append(Component.text("activados").color(NamedTextColor.GREEN).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
                            .append(Component.text(" para ti.").color(NamedTextColor.GREEN)));
        }

        return true;
    }
}

package com.diacero.chatbubbles;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class ChatBubbleTabCompleter implements TabCompleter {

    public ChatBubbleTabCompleter(DCChatBubbles plugin) {
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            String input = args[0].toLowerCase();

            if ("reload".startsWith(input) && sender.hasPermission("dc.chatbubble.reload")) {
                suggestions.add("reload");
            }
            if ("global".startsWith(input) && sender.hasPermission("dc.chatbubble.global")) {
                suggestions.add("global");
            }
        }

        return suggestions;
    }
}

package com.diacero.chatbubbles;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatBubbleListener implements Listener {

    private int MAX_CHARS;
    private long DISPLAY_TICKS;
    private float BUBBLE_Y;
    private float SCALE;
    private int LINE_WIDTH;

    private final JavaPlugin plugin;
    private final Map<UUID, BubbleData> bubbles = new HashMap<>();

    private record BubbleData(TextDisplay display, BukkitRunnable follow, BukkitRunnable expire) {
    }

    public ChatBubbleListener(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        MAX_CHARS = plugin.getConfig().getInt("max_chars", 80);
        DISPLAY_TICKS = plugin.getConfig().getInt("duracion", 6) * 20L;
        BUBBLE_Y = (float) plugin.getConfig().getDouble("altura", 2.35);
        SCALE = (float) plugin.getConfig().getDouble("escala", 0.6);
        LINE_WIDTH = plugin.getConfig().getInt("ancho_linea", 190);
    }

    // ── Eventos ───────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!((DCChatBubbles) plugin).isBubblesEnabled())
            return;
        String plain = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (plain.isBlank())
            return;
        if (plain.length() > MAX_CHARS)
            plain = plain.substring(0, MAX_CHARS - 3) + "...";

        final String message = plain;
        final Player player = event.getPlayer();

        // AsyncChatEvent es async — spawneamos en el hilo principal
        plugin.getServer().getScheduler().runTask(plugin, () -> spawnBubble(player, message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeBubble(event.getPlayer().getUniqueId());
    }

    // ── Lógica ────────────────────────────────────────────────────────────────

    private void spawnBubble(Player player, String message) {
        removeBubble(player.getUniqueId()); // reemplaza el globo anterior si existe

        TextDisplay display = player.getWorld().spawn(bubbleLocation(player), TextDisplay.class, td -> {
            td.text(Component.text(" " + message + " ").color(NamedTextColor.BLACK));
            td.setBillboard(Display.Billboard.CENTER); // siempre mira al jugador
            td.setBackgroundColor(Color.fromARGB(220, 255, 255, 255)); // fondo blanco
            td.setDefaultBackground(false);
            td.setLineWidth(LINE_WIDTH);
            td.setPersistent(false);
            td.setTeleportDuration(4); // interpolación suave al mover
            td.setShadowed(false);
            td.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(0, 0, 0, 1),
                    new Vector3f(SCALE, SCALE, SCALE),
                    new AxisAngle4f(0, 0, 0, 1)));
        });

        // Seguir al jugador cada 2 ticks
        BukkitRunnable follow = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || display.isDead()) {
                    cancel();
                    return;
                }
                display.teleport(bubbleLocation(player));
            }
        };
        follow.runTaskTimer(plugin, 1L, 2L);

        // Eliminar después de X segundos
        BukkitRunnable expire = new BukkitRunnable() {
            @Override
            public void run() {
                removeBubble(player.getUniqueId());
            }
        };
        expire.runTaskLater(plugin, DISPLAY_TICKS);

        bubbles.put(player.getUniqueId(), new BubbleData(display, follow, expire));
    }

    private Location bubbleLocation(Player player) {
        Location loc = player.getLocation();
        double baseY = player.getBoundingBox().getMaxY();
        // BUBBLE_Y suele ser ~2.35. Restamos 1.8 (altura base de jugador de pie)
        // para obtener un offset dinámico sobre la cabeza, sin importar si va en montura, nada o se agacha.
        double offset = BUBBLE_Y - 1.8;
        loc.setY(baseY + offset);
        return loc;
    }

    public void removeBubble(UUID uuid) {
        BubbleData data = bubbles.remove(uuid);
        if (data == null)
            return;
        data.follow().cancel();
        data.expire().cancel();
        if (!data.display().isDead())
            data.display().remove();
    }

    public void removeAll() {
        new HashMap<>(bubbles).keySet().forEach(this::removeBubble);
    }
}

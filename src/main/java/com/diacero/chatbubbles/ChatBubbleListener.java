package com.diacero.chatbubbles;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

public class ChatBubbleListener implements Listener {

    private int MAX_CHARS;
    private long DISPLAY_TICKS;
    private float BUBBLE_Y;
    private float SCALE;
    private int LINE_WIDTH;
    private Color BG_COLOR;
    private TextColor TEXT_COLOR;
    private boolean TEXT_SHADOW;
    private int MAX_BUBBLES;
    private double STACK_DISTANCE;

    private final JavaPlugin plugin;
    // Map para guardar la lista de globos activos de cada jugador
    private final Map<UUID, LinkedList<BubbleData>> playerBubbles = new HashMap<>();

    // Clase auxiliar para guardar las tareas y datos asociados a la burbuja
    private static class BubbleData {
        TextDisplay display;
        BukkitRunnable follow;
        BukkitRunnable expire;
        double stackOffset = 0.0;

        BubbleData(TextDisplay display, BukkitRunnable follow, BukkitRunnable expire) {
            this.display = display;
            this.follow = follow;
            this.expire = expire;
        }
    }

    public ChatBubbleListener(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfig();
    }

    public void reloadConfig() {
        MAX_CHARS = plugin.getConfig().getInt("max_chars", 80);
        DISPLAY_TICKS = plugin.getConfig().getInt("duracion", 6) * 20L;
        BUBBLE_Y = (float) plugin.getConfig().getDouble("altura", 2.2);
        SCALE = (float) plugin.getConfig().getDouble("escala", 1.0);
        LINE_WIDTH = plugin.getConfig().getInt("ancho_linea", 150);
        
        String hexBg = plugin.getConfig().getString("color_fondo", "#FFFFFF");
        int alphaBg = plugin.getConfig().getInt("opacidad_fondo", 220);
        BG_COLOR = parseBukkitColor(hexBg, alphaBg);
        
        String hexText = plugin.getConfig().getString("color_texto", "#000000");
        TEXT_COLOR = TextColor.fromHexString(hexText);
        if (TEXT_COLOR == null) TEXT_COLOR = TextColor.color(0, 0, 0);
        
        TEXT_SHADOW = plugin.getConfig().getBoolean("sombra_texto", false);
        MAX_BUBBLES = plugin.getConfig().getInt("limite_burbujas", 5);
        STACK_DISTANCE = plugin.getConfig().getDouble("distancia_apilado", 0.3);
    }

    private Color parseBukkitColor(String hex, int alpha) {
        try {
            if (hex.startsWith("#")) hex = hex.substring(1);
            if (hex.length() == 6) {
                int r = Integer.valueOf(hex.substring(0, 2), 16);
                int g = Integer.valueOf(hex.substring(2, 4), 16);
                int b = Integer.valueOf(hex.substring(4, 6), 16);
                return Color.fromARGB(alpha, r, g, b);
            }
        } catch (Exception e) {
            // Fallback en caso de error
        }
        return Color.fromARGB(alpha, 255, 255, 255);
    }

    // ── Eventos ───────────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        if (!((DCChatBubbles) plugin).isGlobalEnabled())
            return;

        final Player player = event.getPlayer();
        
        // Si el jugador apagó sus propias burbujas, no generamos burbujas para él
        if (((DCChatBubbles) plugin).isPlayerHidden(player.getUniqueId()))
            return;

        String plain = PlainTextComponentSerializer.plainText().serialize(event.message());
        if (plain.isBlank())
            return;
        if (plain.length() > MAX_CHARS)
            plain = plain.substring(0, MAX_CHARS - 3) + "...";

        final String message = plain;

        // AsyncChatEvent es async — spawneamos en el hilo principal
        plugin.getServer().getScheduler().runTask(plugin, () -> spawnBubble(player, message));
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeAllBubbles(event.getPlayer().getUniqueId());
    }

    // ── Lógica ────────────────────────────────────────────────────────────────

    private void spawnBubble(Player player, String message) {
        UUID uuid = player.getUniqueId();
        LinkedList<BubbleData> list = playerBubbles.computeIfAbsent(uuid, k -> new LinkedList<>());

        // Limitar globos
        if (list.size() >= MAX_BUBBLES) {
            BubbleData oldest = list.removeFirst();
            oldest.expire.cancel();
            oldest.expire.run(); // Ejecuta la animación de salida inmediatamente
        }

        // Empujar los globos existentes hacia arriba
        for (BubbleData bd : list) {
            bd.stackOffset += STACK_DISTANCE;
        }

        TextDisplay display = player.getWorld().spawn(bubbleLocation(player), TextDisplay.class, td -> {
            td.text(Component.text(" " + message + " ").color(TEXT_COLOR));
            td.setBillboard(Display.Billboard.CENTER); // siempre mira al jugador
            td.setBackgroundColor(BG_COLOR); 
            td.setDefaultBackground(false);
            td.setLineWidth(LINE_WIDTH);
            td.setPersistent(false);
            td.setTeleportDuration(4); // interpolación suave al mover
            td.setShadowed(TEXT_SHADOW);
            // Iniciamos con escala 0 para el efecto pop-in
            td.setTransformation(new Transformation(
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(0, 0, 0, 1),
                    new Vector3f(0, 0, 0),
                    new AxisAngle4f(0, 0, 0, 1)));
        });

        // Ocultar la entidad físicamente a los jugadores que desactivaron la opción
        for (UUID hiddenUuid : ((DCChatBubbles) plugin).getHiddenPlayers()) {
            Player hiddenPlayer = plugin.getServer().getPlayer(hiddenUuid);
            if (hiddenPlayer != null && hiddenPlayer.isOnline()) {
                hiddenPlayer.hideEntity(plugin, display);
            }
        }

        // Aplicamos la animación premium de crecimiento (Pop-in) con 1 tick de retraso para que el cliente la dibuje
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!display.isDead()) {
                display.setInterpolationDuration(7); // 7 ticks para que nazca más suavemente
                display.setInterpolationDelay(0);
                display.setTransformation(new Transformation(
                        new Vector3f(0, 0, 0),
                        new AxisAngle4f(0, 0, 0, 1),
                        new Vector3f(SCALE, SCALE, SCALE),
                        new AxisAngle4f(0, 0, 0, 1)));
            }
        }, 1L);

        // Variables temporales para pasar a la clase de datos (arreglo de 1 elemento para poder ser referenciado en lambdas)
        final BubbleData[] dataContainer = new BubbleData[1];

        // Seguir al jugador cada 2 ticks
        BukkitRunnable follow = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline() || display.isDead()) {
                    cancel();
                    return;
                }
                Location loc = bubbleLocation(player);
                if (dataContainer[0] != null) {
                    loc.add(0, dataContainer[0].stackOffset, 0); // Aplica el offset de apilamiento
                }
                display.teleport(loc);
            }
        };
        follow.runTaskTimer(plugin, 1L, 2L);

        // Eliminar después de X segundos con animación de encogimiento (Pop-out)
        BukkitRunnable expire = new BukkitRunnable() {
            @Override
            public void run() {
                if (!display.isDead()) {
                    display.setInterpolationDuration(10); // 10 ticks al desaparecer
                    display.setInterpolationDelay(0);
                    display.setTransformation(new Transformation(
                            new Vector3f(0, 0, 0),
                            new AxisAngle4f(0, 0, 0, 1),
                            new Vector3f(0, 0, 0), // Escala vuelve a 0
                            new AxisAngle4f(0, 0, 0, 1)));
                }
                
                // Borrar la entidad por completo y remover de la lista activa
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    if (dataContainer[0] != null) {
                        dataContainer[0].follow.cancel();
                        if (!display.isDead()) display.remove();
                        LinkedList<BubbleData> currentList = playerBubbles.get(uuid);
                        if (currentList != null) {
                            currentList.remove(dataContainer[0]);
                            if (currentList.isEmpty()) {
                                playerBubbles.remove(uuid);
                            }
                        }
                    }
                }, 11L);
            }
        };
        expire.runTaskLater(plugin, DISPLAY_TICKS);

        BubbleData newData = new BubbleData(display, follow, expire);
        dataContainer[0] = newData;
        list.addLast(newData);
    }

    private Location bubbleLocation(Player player) {
        if (player.isInsideVehicle() && player.getVehicle() != null) {
            // En monturas (camellos, caballos), Spigot calcula la altura desde las patas.
            // Solución: Usar la parte más alta de la caja de colisión del animal como base.
            Location loc = player.getLocation();
            double vehicleTop = player.getVehicle().getBoundingBox().getMaxY();
            
            // Sumamos 0.7 (altura aprox de la cabeza del jugador sentado) + tu offset de configuración
            double sittingOffset = 0.7 + (BUBBLE_Y - 1.8);
            loc.setY(vehicleTop + sittingOffset);
            return loc;
        } else {
            // Jugador a pie, volando, nadando o agachado
            Location loc = player.getEyeLocation();
            double offset = BUBBLE_Y - 1.62;
            loc.add(0, offset, 0);
            return loc;
        }
    }

    public void removeAllBubbles(UUID uuid) {
        LinkedList<BubbleData> list = playerBubbles.remove(uuid);
        if (list == null)
            return;
        for (BubbleData data : list) {
            data.follow.cancel();
            data.expire.cancel();
            if (!data.display.isDead())
                data.display.remove();
        }
    }

    public void removeAll() {
        new HashMap<>(playerBubbles).keySet().forEach(this::removeAllBubbles);
    }
}

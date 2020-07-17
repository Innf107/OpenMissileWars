package org.astropeci.omw.item;

import com.destroystokyo.paper.Title;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.astropeci.omw.teams.GlobalTeamManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PeriodicItemDispenser implements AutoCloseable {

    private static final int ITEM_DROP_DELAY = 20 * 15;

    private final World world;
    private final GlobalTeamManager globalTeamManager;
    private final Plugin plugin;

    private final Set<ItemStack> items = Set.of(
            simpleItem(Material.CREEPER_SPAWN_EGG, "Tomahawk"),
            simpleItem(Material.GHAST_SPAWN_EGG, "Juggernaut"),
            simpleItem(Material.WITCH_SPAWN_EGG, "Shieldbuster"),
            simpleItem(Material.OCELOT_SPAWN_EGG, "Lightning"),
            simpleItem(Material.GUARDIAN_SPAWN_EGG, "Guardian"),
            simpleItem(Material.BLAZE_SPAWN_EGG, "Fireball"),
            simpleItem(Material.SNOWBALL, "Shield")
    );

    private boolean shouldRun = true;

    public void start() {
        if (shouldStart()) {
            TextComponent message = new TextComponent("Game is starting in 10 seconds");
            message.setColor(ChatColor.GREEN);
            world.getPlayers().forEach(player -> player.sendMessage(message));

            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                TextComponent titleMessage;

                boolean shouldStart = shouldStart();
                if (shouldStart) {
                    titleMessage = new TextComponent("Game is starting");
                    titleMessage.setColor(ChatColor.GREEN);
                } else {
                    titleMessage = new TextComponent("Not enough players to start");
                    titleMessage.setColor(ChatColor.RED);
                }

                Title title = new Title(titleMessage, null, 5, 40, 20);

                world.getPlayers().forEach(player -> player.sendTitle(title));

                if (shouldStart) {
                    giveItemsPeriodically();
                } else {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::start, 1);
                }
            }, 20 * 10);
        } else {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::start, 1);
        }
    }

    private boolean shouldStart() {
        long occupiedTeams = world.getPlayers().stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .flatMap(player -> globalTeamManager.getPlayerTeam(player).stream())
                .distinct()
                .count();

        return occupiedTeams >= 1;
    }

    private void giveItemsPeriodically() {
        if (!shouldRun) {
            Bukkit.getLogger().info("Shutting down item dispenser");
            return;
        }

        Set<Player> players = world.getPlayers().stream()
                .filter(player -> globalTeamManager.getPlayerTeam(player).isPresent())
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .collect(Collectors.toSet());

        if (new Random().nextInt(4) == 0) {
            for (Player player : players) {
                PlayerInventory inventory = player.getInventory();

                for (ItemStack stack : inventory.getContents()) {
                    if (stack == null) continue;

                    if (stack.getType() == Material.BUCKET) {
                        stack.setType(Material.WATER_BUCKET);
                    }
                }
            }
        } else {
            List<ItemStack> itemList = new ArrayList<>(items);
            Collections.shuffle(itemList);

            ItemStack item = itemList.get(0);

            for (Player player : players) {
                PlayerInventory inventory = player.getInventory();

                if (!inventory.contains(item.getType()) && inventory.getItemInOffHand().getType() != item.getType()) {
                    inventory.addItem(item);
                }
            }
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this::giveItemsPeriodically, ITEM_DROP_DELAY);
    }

    private ItemStack simpleItem(Material material, String name) {
        ItemStack stack = new ItemStack(material);

        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.RESET + name);
        stack.setItemMeta(meta);

        return stack;
    }

    @Override
    public void close() {
        shouldRun = false;
    }
}

package org.astropeci.omw.worlds;

import lombok.RequiredArgsConstructor;
import org.astropeci.omw.FileUtil;
import org.astropeci.omw.teams.GlobalTeamManager;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

@RequiredArgsConstructor
public class WorldManager {

    private final GlobalTeamManager globalTeamManager;

    public World getDefaultWorld() {
        return Bukkit.getWorlds().get(0);
    }

    public void configureWorld(World world) {
        world.setDifficulty(Difficulty.EASY);
        world.setSpawnLocation(0, 64, 0);
        world.setKeepSpawnInMemory(false);

        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);

        world.setFullTime(18000);
    }

    public void cleanArenas() {
        Path worldContainer = Bukkit.getServer().getWorldContainer().toPath();

        try {
            Files.list(worldContainer)
                    .parallel()
                    .filter(Files::isDirectory)
                    .filter(path -> path.getFileName().toString().startsWith("omw-arena-"))
                    .forEach(directory -> {
                        try {
                            Bukkit.getLogger().info("Found dangling arena in directory " + directory);
                            FileUtil.deleteRecursive(directory);
                        } catch (IOException e) {
                            Bukkit.getLogger().log(Level.WARNING, "Failed to delete dangling arena", e);
                        }
                    });
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Failed to check for dangling arenas", e);
        }
    }

    public void send(Player player, World world) {
        Location spawnPoint = world.getSpawnLocation();
        Location alignedSpawnPoint = new Location(world, spawnPoint.getX() + 0.5, spawnPoint.getY(), spawnPoint.getZ() + 0.5, -90, 0);

        send(player, alignedSpawnPoint);
    }

    public void send(Player player, Location location) {
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.setFlying(false);
        player.setExp(0);
        player.setLevel(0);
        player.setFallDistance(0);
        player.setVelocity(new Vector());
        player.setHealth(20);
        player.setPlayerWeather(WeatherType.DOWNFALL);

        globalTeamManager.removePlayerFromTeam(player);

        player.teleport(location);
    }
}

package org.astropeci.omw.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBucketFillEvent;

import java.util.Set;

public class WaterPickupHandler implements Listener {

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent e) {
        Location location = e.getBlock().getLocation();
        removeWaterAt(location);
    }

    private void removeWaterAt(Location location) {
        World world = location.getWorld();
        Block block = world.getBlockAt(location);

        if (block.getType() == Material.WATER) {
            block.setType(Material.AIR);

            Set<Location> adjacentLocations = Set.of(
                    location.clone().add(1, 0, 0),
                    location.clone().add(-1, 0, 0),
                    location.clone().add(0, 1, 0),
                    location.clone().add(0, -1, 0),
                    location.clone().add(0, 0, 1),
                    location.clone().add(0, 0, -1)
            );

            for (Location adjacent : adjacentLocations) {
                removeWaterAt(adjacent);
            }
        }
    }
}

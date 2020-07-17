package org.astropeci.omw.listeners;

import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class FishingHandler implements Listener {

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        Player player = e.getPlayer();
        Entity caught = e.getCaught();

        if (caught instanceof TNTPrimed) {
            Vector velocity = caught.getVelocity();
            velocity.add(new Vector(0, 0.4, 0));
            caught.setVelocity(velocity);

            caught.setGravity(false);
        }

        if (caught instanceof Item) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 2400, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 2400, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 2400, 0));

            e.setExpToDrop(0);
            caught.remove();

            player.playSound(caught.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 5, 0);
        }
    }
}

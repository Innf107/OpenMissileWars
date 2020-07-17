package org.astropeci.omw.item;

import org.astropeci.omw.teams.GameTeam;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Set;

public class EquipmentProvider {

    public void giveToPlayer(Player player, GameTeam team) {
        Color color = team == GameTeam.GREEN ? Color.fromRGB(29, 143, 97) : Color.fromRGB(96, 32, 135);

        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);

        Set<ItemStack> armourPieces = Set.of(helmet, chestplate, leggings, boots);

        for (ItemStack armourPiece : armourPieces) {
            ItemMeta meta = armourPiece.getItemMeta();
            ((LeatherArmorMeta) meta).setColor(color);
            meta.setUnbreakable(true);
            armourPiece.setItemMeta(meta);
        }

        ItemStack rod = new ItemStack(Material.FISHING_ROD);

        ItemMeta rodMeta = rod.getItemMeta();
        rodMeta.setDisplayName(ChatColor.RESET + "FishingBlade");
        rod.setItemMeta(rodMeta);

        rod.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, 4);
        rod.addUnsafeEnchantment(Enchantment.DURABILITY, 10);

        ItemStack bucket = new ItemStack(Material.BUCKET);

        PlayerInventory inventory = player.getInventory();

        inventory.setHelmet(helmet);
        inventory.setChestplate(chestplate);
        inventory.setLeggings(leggings);
        inventory.setBoots(boots);

        inventory.addItem(rod, bucket);
    }
}

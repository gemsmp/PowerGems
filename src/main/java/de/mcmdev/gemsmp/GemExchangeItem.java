package de.mcmdev.gemsmp;

import dev.iseal.powergems.PowerGems;
import dev.iseal.powergems.managers.GemManager;
import org.bukkit.*;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class GemExchangeItem implements Listener {

    public void init() {
        PowerGems plugin = JavaPlugin.getPlugin(PowerGems.class);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.getCommand("getgemexchangeitem").setExecutor(new Command());
    }

    private ItemStack getGemExchangeItem() {
        ItemStack itemStack = new ItemStack(Material.GLASS_BOTTLE);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.YELLOW + "Gem-Tauschitem");
        itemMeta.setLore(Arrays.asList(
                ChatColor.RED + "Bewege einen Gem auf dieses Item,",
                ChatColor.RED + "um den Gem einzutauschen."
        ));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event)   {
        if(!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (event.getCurrentItem() == null || !event.getCurrentItem().isSimilar(getGemExchangeItem())) {
            return;
        }

        if (event.getCursor() == null || !GemManager.getInstance().isGem(event.getCursor())) {
            // Cursor item is not a gem, do nothing
            return;
        }

        if(event.getCurrentItem().getAmount() > 1 || event.getCursor().getAmount() > 1) {
            // Don't do anything if the exchange items are stacked to prevent buggy behavior.
            return;
        }

        event.setCancelled(true);

        int gemLevel = GemManager.getInstance().getLevel(event.getCursor());

        event.setCursor(null);
        event.setCurrentItem(GemManager.getInstance().createGemWithLevel(gemLevel));

        player.playSound(player, Sound.ENTITY_ILLUSIONER_CAST_SPELL, SoundCategory.PLAYERS, 1, 1);
    }

    public class Command implements CommandExecutor {

        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
            if (!(sender instanceof Player player)) {
                return true;
            }

            player.getInventory().addItem(GemExchangeItem.this.getGemExchangeItem());
            return true;
        }
    }

}

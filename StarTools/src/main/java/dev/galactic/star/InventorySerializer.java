package dev.galactic.star;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;

public class InventorySerializer {

    /**
     * Converts the Bukkit Inventory into a byte array.
     *
     * @param inventory Bukkit Inventory.
     * @return Byte array.
     */
    public static byte[] serialize(Inventory inventory) {
        return ObjectSerializer.objectToByteArray(new StarInventory(inventory));
    }

    /**
     * Converts byte array to Bukkit inventory.
     *
     * @param data Byte array.
     * @return Bukkit inventory object.
     */
    public static Inventory deserialize(byte[] data) {
        return getInventory((StarInventory) ObjectSerializer.byteArrayToObject(data));
    }

    /**
     * Returns a Bukkit inventory from the StarInventory object.
     *
     * @param starInventory Serializable Bukkit Inventory.
     * @return Bukkit Inventory.
     */
    private static Inventory getInventory(StarInventory starInventory) {
        Inventory inventory;
        if (starInventory.size == 0) {
            inventory = Bukkit.createInventory(
                    starInventory.inventoryHolder,
                    starInventory.inventoryType,
                    starInventory.title
            );
        } else {
            inventory = Bukkit.createInventory(
                    starInventory.inventoryHolder,
                    starInventory.size,
                    starInventory.title
            );
        }
        inventory.setContents(starInventory.contents);
        inventory.setMaxStackSize(starInventory.maxStackSize);
        return inventory;
    }

    private static class StarInventory implements Serializable {
        private final ItemStack[] contents;
        private final InventoryHolder inventoryHolder;
        private final int maxStackSize;
        private final int size;
        private final String title;
        private final InventoryType inventoryType;

        public StarInventory(Inventory inventory) {
            this.contents = inventory.getContents();
            this.inventoryHolder = inventory.getHolder();
            this.maxStackSize = inventory.getMaxStackSize();
            this.size = inventory.getSize();
            this.title = inventory.getTitle();
            this.inventoryType = inventory.getType();
        }
    }
}

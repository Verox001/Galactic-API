/*
 * Copyright 2023 Galactic Star Studios
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.galactic.star.tools;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.*;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * The Class used to serialize Bukkit Inventories and convert it to a byte array or to Base64.
 *
 * @author PrismoidNW
 */
public class InventorySerializer {

    /**
     * The default constructor. There is no need to instantiate this class.
     *
     * @throws InstantiationException When you instantiate this class.
     */
    public InventorySerializer() throws InstantiationException {
        throw new InstantiationException("You shouldn't instantiate this class as it is a utility class.");
    }

    /**
     * Converts the Bukkit Inventory into a byte array.
     *
     * @param inventory Bukkit Inventory.
     * @return Byte array.
     */
    public static byte[] serialize(Inventory inventory) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

            dataOutput.writeUTF(getTypeString(inventory));

            for (ItemStack item : inventory.getContents()) {
                dataOutput.writeObject(item);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Converts a Bukkit Inventory to Base64 String.
     *
     * @param inventory Bukkit Inventory.
     * @return String
     */
    public static String toBase64(Inventory inventory) {
        return Base64.encode(serialize(inventory));
    }


    /**
     * Converts a Base64 String into Bukkit inventory.
     *
     * @param data String.
     * @return Bukkit Inventory.
     */
    public static Inventory fromBase64(String data) {
        return deserialize(Base64.decode(data));
    }

    /**
     * Converts byte array to Bukkit inventory.
     *
     * @param data Byte array.
     * @return Bukkit inventory object.
     */
    public static Inventory deserialize(byte[] data) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, getType(dataInput.readUTF()));

            // Read the serialized inventory
            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }

            dataInput.close();
            return inventory;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static InventoryType getType(String type) {
        switch (type.toLowerCase()) {
            case "furnace": {
                return InventoryType.FURNACE;
            }
            case "player": {
                return InventoryType.PLAYER;
            }
            case "crafting": {
                return InventoryType.CRAFTING;
            }
            case "enchanting": {
                return InventoryType.ENCHANTING;
            }
            case "brewing": {
                return InventoryType.BREWING;
            }
            case "merchant": {
                return InventoryType.MERCHANT;
            }
            case "chest": {
                return InventoryType.CHEST;
            }
            case "anvil": {
                return InventoryType.ANVIL;
            }
            case "beacon": {
                return InventoryType.BEACON;
            }
        }
        return null;
    }

    private static String getTypeString(Inventory inventory) {
        if (inventory instanceof PlayerInventory) return "player";
        else if (inventory instanceof FurnaceInventory) return "furnace";
        else if (inventory instanceof CraftingInventory) return "crafting";
        else if (inventory instanceof EnchantingInventory) return "enchanting";
        else if (inventory instanceof BrewerInventory) return "brewing";
        else if (inventory instanceof MerchantInventory) return "merchant";
        else if (inventory instanceof DoubleChestInventory) return "chest";
        else if (inventory instanceof AnvilInventory) return "anvil";
        else if (inventory instanceof BeaconInventory) return "beacon";
        return null;
    }
}

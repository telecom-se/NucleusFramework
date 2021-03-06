/*
 * This file is part of NucleusFramework for Bukkit, licensed under the MIT License (MIT).
 *
 * Copyright (c) JCThePants (www.jcwhatever.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */


package com.jcwhatever.nucleus.internal.managed.scripting.api;

import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.inventory.InventoryUtils;
import com.jcwhatever.nucleus.utils.items.ItemStackMatcher;
import com.jcwhatever.nucleus.utils.materials.NamedMaterialData;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.List;
import javax.annotation.Nullable;

/**
 * Provide scripts with API access to chest helper functions.
 */
public class SAPI_Inventory implements IDisposable {

    private boolean _isDisposed;

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        _isDisposed = true;
    }

    /**
     * Get an {@link ItemStack} comparer.
     *
     * @param operations  The compare operations to perform.
     */
    public ItemStackMatcher getComparer(byte operations) {
        return ItemStackMatcher.get(operations);
    }

    /**
     * Get the default item stack comparer.
     */
    public ItemStackMatcher getDefaultComparer() {
        return ItemStackMatcher.getDefault();
    }

    /**
     * Get the durability/meta/type item stack comparer.
     */
    public ItemStackMatcher getDurabilityComparer() {
        return ItemStackMatcher.getDefault();
    }

    /**
     * Get the maximum number of the specified item stack that will fit into
     * the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     */
    public int getMax(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer) {
        return InventoryUtils.getMax(inventory, itemStack, comparer);
    }

    /**
     * Determine if there is enough room in the specified inventory
     * for the specified stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     */
    public boolean hasRoom(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer) {
        return InventoryUtils.hasRoom(inventory, itemStack, comparer, itemStack.getAmount());
    }

    /**
     * Determine if there is enough room in the specified inventory for
     * items of the same type of the specified stack in the amount of
     * the specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check .
     * @param comparer   The {@link ItemStackMatcher} to use.
     * @param qty        The amount of space needed.
     */
    public boolean hasRoomForQty(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer, int qty) {
        return InventoryUtils.hasRoom(inventory, itemStack, comparer, qty);
    }

    /**
     * Get the number of items of the specified item stack are in the
     * specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     */
    public int count(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer) {
        return InventoryUtils.count(inventory, itemStack, comparer);
    }

    /**
     * Determine if the inventory contents have at least one of the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     */
    public boolean has(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer) {
        return InventoryUtils.has(inventory, itemStack, comparer);
    }

    /**
     * Determine if the inventory contents has at least the specified quantity of
     * the specified item stack.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     * @param qty        The quantity.
     */
    public boolean hasQty(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer, int qty) {
        return InventoryUtils.has(inventory, itemStack, comparer, qty);
    }

    /**
     * Get an item stack array representing all stacks of the specified item
     * from the specified inventory.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     */
    public ItemStack[] getAll(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer) {
        return InventoryUtils.getAll(inventory, itemStack, comparer);
    }

    /**
     * Remove items from the specified inventory that match the specified
     * item stack in the specified quantity.
     *
     * @param inventory  The inventory to check.
     * @param itemStack  The item stack to check.
     * @param comparer   The {@link ItemStackMatcher} to use.
     * @param qty        The quantity to remove.
     */
    public List<ItemStack> remove(Inventory inventory, ItemStack itemStack, ItemStackMatcher comparer, int qty) {
        return InventoryUtils.removeAmount(inventory, itemStack, comparer, qty);
    }

    /**
     * Create an item stack with the specified material name and optional data.
     *
     * @param materialName  The {@link Material} enum or material name.
     * @param data          Optional. The material data value.
     *
     * @see NamedMaterialData
     */
    public ItemStack createItem(Object materialName, @Nullable Object data) {
        PreCon.notNull(materialName, "materialName");

        MaterialData materialData;

        if (materialName instanceof String) {
            materialData = NamedMaterialData.get((String)materialName);
            PreCon.isValid(materialData != null, "materialName is invalid: {0}", materialName);
        }
        else {
            Material material = EnumUtils.getEnum(materialName, Material.class);
            materialData = new MaterialData(material);
        }

        if (data != null) {

            if (data instanceof Number) {
                materialData.setData(((Number)data).byteValue());
            }
            else if (data instanceof String) {
                materialData.setData(TextUtils.parseByte((String)data, (byte)0));
            }
        }

        return materialData.toItemStack();
    }
}


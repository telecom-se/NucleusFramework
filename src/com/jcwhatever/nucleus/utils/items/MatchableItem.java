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


package com.jcwhatever.nucleus.utils.items;

import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * An {@link org.bukkit.inventory.ItemStack} wrapper.
 *
 * <p>Provides built in {@link ItemStackMatcher} support in the {@link #equals}
 * and {@link #hashCode} methods making the wrapper ideal for use as a hash key
 * that represents the item.</p>
 *
 * <p>{@link #hashCode} method returns the hash based on the encapsulated
 * {@link org.bukkit.inventory.ItemStack}'s {@link org.bukkit.Material} type so
 * that different {@link ItemStackMatcher} compare operations can be used to find
 * an {@link org.bukkit.inventory.ItemStack} by key or in a hash set.</p>
 *
 */
public class MatchableItem {

    private ItemStack _itemStack;
    private ItemStackMatcher _matcher;

    /**
     * Constructor.
     *
     * <p>Uses the default {@link ItemStackMatcher}.</p>
     *
     * @param itemStack  The {@link ItemStack} to encapsulate.
     */
    public MatchableItem(ItemStack itemStack) {
        this(itemStack, ItemStackMatcher.getDefault());
    }

    /**
     * Constructor.
     *
     * @param itemStack  The {@link ItemStack} to encapsulate.
     * @param matcher    The matcher to use.
     */
    public MatchableItem(ItemStack itemStack, ItemStackMatcher matcher) {
        PreCon.notNull(itemStack);
        PreCon.notNull(matcher );

        setItem(itemStack);
        setMatcher(matcher);
    }

    /**
     * Constructor.
     */
    protected MatchableItem(ItemStackMatcher matcher) {
        PreCon.notNull(matcher);

        setMatcher(matcher);
    }

    /**
     * Get the encapsulated {@link org.bukkit.inventory.ItemStack}.
     */
    public ItemStack getItem() {
        return _itemStack;
    }

    /**
     * Get the material type.
     */
    public Material getMaterial() {
        return _itemStack.getType();
    }

    /**
     * Get the {@link ItemStackMatcher}.
     */
    public ItemStackMatcher getItemStackMatcher() {
        return _matcher;
    }

    @Override
    public int hashCode() {
        if (_itemStack == null)
            return 0;

        return 31 + _itemStack.getTypeId();
    }

    @Override
    public boolean equals(Object o) {
        if (_itemStack == null)
            return false;

        if (o instanceof ItemStack) {
            return _matcher.isMatch(_itemStack, (ItemStack) o);
        }
        else if (o instanceof MatchableItem) {
            MatchableItem wrapper = (MatchableItem)o;

            return _matcher.isMatch(_itemStack, wrapper.getItem());
        }

        return false;
    }

    /**
     * Set the current matchable {@link ItemStack}.
     *
     * @param itemStack  The {@link ItemStack}.
     */
    protected void setItem(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        _itemStack = itemStack;
    }

    /**
     * Set the current {@link ItemStackMatcher}.
     *
     * @param matcher  The {@link ItemStackMatcher}.
     */
    protected void setMatcher(ItemStackMatcher matcher) {
        PreCon.notNull(matcher);

        _matcher = matcher;
    }
}

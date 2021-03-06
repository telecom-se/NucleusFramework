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

package com.jcwhatever.nucleus.internal.managed.items.meta;

import com.jcwhatever.nucleus.managed.items.meta.IItemMetaHandler;
import com.jcwhatever.nucleus.managed.items.meta.ItemMetaValue;
import com.jcwhatever.nucleus.utils.PreCon;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles a book {@link org.bukkit.inventory.ItemStack} author meta.
 *
 * @see InternalItemMetaHandlers
 */
class ItemBookAuthor implements IItemMetaHandler {

    @Override
    public String getMetaName() {
        return "bookAuthor";
    }

    @Override
    public boolean canHandle(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        ItemMeta meta = itemStack.getItemMeta();
        return meta instanceof BookMeta && ((BookMeta) meta).getAuthor() != null;
    }

    @Override
    public boolean apply(ItemStack itemStack, ItemMetaValue meta) {
        PreCon.notNull(itemStack);
        PreCon.notNull(meta);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof BookMeta))
            return false;

        BookMeta bookMeta = (BookMeta)itemMeta;

        bookMeta.setAuthor(meta.getRawData());

        itemStack.setItemMeta(bookMeta);

        return true;
    }

    @Override
    public List<ItemMetaValue> getMeta(ItemStack itemStack) {
        PreCon.notNull(itemStack);

        List<ItemMetaValue> result = new ArrayList<>(1);

        ItemMeta itemMeta = itemStack.getItemMeta();
        if (!(itemMeta instanceof BookMeta))
            return result;

        BookMeta bookMeta = (BookMeta)itemMeta;
        if (bookMeta.getAuthor() == null)
            return result;

        result.add(new ItemMetaValue(getMetaName(), bookMeta.getAuthor()));

        itemStack.setItemMeta(bookMeta);

        return result;
    }
}

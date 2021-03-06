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


package com.jcwhatever.nucleus.utils.converters;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;

import javax.annotation.Nullable;

/**
 * Converts a {@link org.bukkit.inventory.ItemStack} or {@link org.bukkit.potion.Potion}
 * to potion ID.
 */
public class PotionIDConverter extends Converter<Short> {

    protected PotionIDConverter() {}

    @Nullable
    @Override
    protected Short onConvert(@Nullable Object value) {

        if (value instanceof ItemStack) {
            Potion potion = Potion.fromItemStack((ItemStack)value);
            if (potion == null)
                return null;

            return potion.toDamageValue();
        }

        if (value instanceof Potion) {
            Potion potion = (Potion)value;
            return potion.toDamageValue();
        }
        return 8192;
    }
}

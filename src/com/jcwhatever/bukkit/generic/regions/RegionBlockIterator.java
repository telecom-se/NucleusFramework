/* This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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


package com.jcwhatever.bukkit.generic.regions;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;

/**
 * Iterates through all blocks in a region.
 */
public class RegionBlockIterator implements Iterator<Block> {

    private int _currentY;
    private int _currentX;
    private int _currentZ;
    private Block _current;

    private Region _region;

    /**
     * Constructor.
     *
     * @param region  The region to iterate.
     */
    public RegionBlockIterator (Region region) {
        _region = region;

        _currentY = region.getYStart();
        _currentX = region.getXStart();
        _currentZ = region.getZStart();
    }

    /**
     * Constructor.
     *
     * @param region  The region to iterate.
     */
    public RegionBlockIterator (ReadOnlyRegion region) {
        _region = region.getHandle();

        _currentY = region.getYStart();
        _currentX = region.getXStart();
        _currentZ = region.getZStart();
    }

    /**
     * Determine if there is a next block.
     */
    @Override
    public boolean hasNext() {
        if (_region.getWorld() == null)
            return false;

        if (_currentY > _region.getYEnd())
            return false;

        return true;
    }

    /**
     * Get the next block.
     */
    @Override
    public Block next() {

        if (!hasNext())
            return null;

        _current = _region.getWorld().getBlockAt(_currentX, _currentY, _currentZ);

        _currentZ++;

        if (_currentZ > _region.getZEnd()) {
            _currentZ = _region.getZStart();
            _currentX++;

            if (_currentX > _region.getXEnd()) {
                _currentX = _region.getXStart();

                _currentY++;
            }
        }

        return _current;

    }

    /**
     * Remove the current block. Sets the block material
     * to {@code AIR}.
     */
    @Override
    public void remove() {
        if (_current != null)
            _current.setType(Material.AIR);
    }
}

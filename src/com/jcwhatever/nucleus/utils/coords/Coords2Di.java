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

package com.jcwhatever.nucleus.utils.coords;

import com.jcwhatever.nucleus.storage.IDataNode;
import com.jcwhatever.nucleus.storage.serialize.DeserializeException;
import com.jcwhatever.nucleus.storage.serialize.IDataNodeSerializable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.file.IByteSerializable;
import com.jcwhatever.nucleus.utils.file.IByteReader;
import com.jcwhatever.nucleus.utils.file.IByteWriter;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;
import javax.annotation.Nullable;

/**
 * 2D immutable integer coordinates.
 */
public class Coords2Di implements ICoords2Di, IDataNodeSerializable, IByteSerializable {

    /**
     * Get a {@link Coords2Di} from a {@link org.bukkit.Chunk}.
     *
     * @param chunk The chunk to convert.
     */
    public static Coords2Di fromChunk(Chunk chunk) {
        PreCon.notNull(chunk);

        return new Coords2Di(chunk.getX(), chunk.getZ());
    }

    /**
     * Get the distance from source coordinates to target coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distance(ICoords2Di source, ICoords2Di target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        return Math.sqrt(distanceSquared(source, target));
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The other coordinates.
     */
    public static double distance(ICoords2Di source, ICoords2D target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        return Math.sqrt(distanceSquared(source, target));
    }

    /**
     * Get distance squared between two coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distanceSquared(ICoords2Di source, ICoords2Di target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        double deltaX = target.getX() - source.getX();
        double deltaZ = target.getZ() - source.getZ();

        return deltaX * deltaX + deltaZ * deltaZ;
    }

    /**
     * Get distance squared between two coordinates.
     *
     * @param source  The source coordinates.
     * @param target  The target coordinates.
     */
    public static double distanceSquared(ICoords2Di source, ICoords2D target) {
        PreCon.notNull(source);
        PreCon.notNull(target);

        double deltaX = target.getX() - source.getX();
        double deltaZ = target.getZ() - source.getZ();

        return deltaX * deltaX + deltaZ * deltaZ;
    }

    /**
     * Get a {@link org.bukkit.Chunk} from the specified {@link org.bukkit.World}
     * at the specified coordinates.
     *
     * @param coords  The chunk coordinates.
     * @param world   The world the chunk is in.
     */
    public static Chunk getChunk(ICoords2Di coords, World world) {
        PreCon.notNull(coords);
        PreCon.notNull(world);

        return world.getChunkAt(coords.getX(), coords.getZ());
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param coords  The coordinates to copy from.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location copyTo(ICoords2Di coords, Location output) {
        PreCon.notNull(coords);
        PreCon.notNull(output);

        output.setX(coords.getX());
        output.setZ(coords.getZ());
        return output;
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param coords  The coords to copy from.
     * @param world   The {@link org.bukkit.World} to put into the output {@link org.bukkit.Location}.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public static Location copyTo(ICoords2Di coords, @Nullable World world, Location output) {
        PreCon.notNull(coords);
        PreCon.notNull(output);

        output.setWorld(world);
        output.setX(coords.getX());
        output.setZ(coords.getZ());
        return output;
    }

    private int _x;
    private int _z;
    private boolean _isImmutable;

    /**
     * Constructor.
     *
     * @param x The x coordinates.
     * @param z The z coordinates.
     */
    public Coords2Di(int x, int z) {
        _x = x;
        _z = z;
        seal();
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates.</p>
     *
     * @param source The source coordinates.
     */
    public Coords2Di(ICoords2Di source) {
        this(source.getX(), source.getZ());
    }

    /**
     * Constructor.
     *
     * <p>Clones values from source coordinates and adds delta values.</p>
     *
     * @param source The source coordinates.
     * @param deltaX The X coordinate values to add to the source coordinates.
     * @param deltaZ The Z coordinate values to add to the source coordinates.
     */
    public Coords2Di(ICoords2Di source, int deltaX, int deltaZ) {
        this(source.getX() + deltaX, source.getZ() + deltaZ);
    }

    /**
     * Protected constructor for serialization.
     */
    protected Coords2Di() {}

    /**
     * Determine if the object is immutable.
     */
    public final boolean isImmutable() {
        return _isImmutable;
    }

    @Override
    public int getX() {
        return _x;
    }

    @Override
    public int getZ() {
        return _z;
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords The other coordinates.
     */
    public double distance(ICoords2D coords) {
        return distance(this, coords);
    }

    /**
     * Get the distance from this coordinates to another coordinates.
     *
     * @param coords The other coordinates.
     */
    public double distance(ICoords2Di coords) {
        return distance(this, coords);
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords The other coordinates.
     */
    public double distanceSquared(ICoords2D coords) {
        return distanceSquared(this, coords);
    }

    /**
     * Get the distance from this coordinates to another coordinates squared.
     *
     * @param coords The other coordinates.
     */
    public double distanceSquared(ICoords2Di coords) {
        return distanceSquared(this, coords);
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords The other coordinates.
     */
    public Coords2Di getDelta(ICoords2Di coords) {
        PreCon.notNull(coords);

        int deltaX = getX() - coords.getX();
        int deltaZ = getZ() - coords.getZ();

        return new Coords2Di(deltaX, deltaZ);
    }

    /**
     * Create delta coordinates by subtracting other coordinates from
     * this coordinates.
     *
     * @param coords  The other coordinates.
     * @param output  The {@link MutableCoords2Di} to put the result into.
     */
    public MutableCoords2Di getDelta(ICoords2Di coords, MutableCoords2Di output) {
        PreCon.notNull(coords);

        int deltaX = getX() - coords.getX();
        int deltaZ = getZ() - coords.getZ();

        output.setX(deltaX);
        output.setZ(deltaZ);

        return output;
    }

    /**
     * Get a {@link org.bukkit.Chunk} from the specified {@link org.bukkit.World}
     * at the coordinates represented by the {@link Coords2D} instance.
     *
     * @param world The world the chunk is in.
     */
    public Chunk getChunk(World world) {
        return getChunk(this, world);
    }

    /**
     * Create a new {@link Coords2D} using the x and z
     * coordinate values.
     */
    public Coords2D to2D() {
        return new Coords2D(getX(), getZ());
    }

    /**
     * Create a new {@link Coords3D} using the x and z
     * coordinate values and the specified y value.
     *
     * @param y  The y coordinate value.
     */
    public Coords3D to3D(double y) {
        return new Coords3D(getX(), y, getZ());
    }

    /**
     * Create a new {@link Coords3Di} instance using the x and
     * z coordinate values and the specified y coordinate values.
     *
     * @param y  The coordinate value.
     */
    public Coords3Di to3Di(int y) {
        return new Coords3Di(getX(), y, getZ());
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public Location copyTo(Location output) {
        return copyTo(this, output);
    }

    /**
     * Copy the X and Z values to an output {@link org.bukkit.Location}.
     *
     * @param world   The {@link org.bukkit.World} to put into the output {@link org.bukkit.Location}.
     * @param output  The output {@link org.bukkit.Location}.
     *
     * @return  The output {@link org.bukkit.Location}.
     */
    public Location copyTo(@Nullable World world, Location output) {
        return copyTo(this, world, output);
    }

    @Override
    public void serialize(IDataNode dataNode) {
        dataNode.set("x", _x);
        dataNode.set("z", _z);
    }

    @Override
    public void deserialize(IDataNode dataNode) throws DeserializeException {
        _x = dataNode.getInteger("x");
        _z = dataNode.getInteger("z");
        seal();
    }

    @Override
    public void serialize(IByteWriter writer) throws IOException {
        writer.write(_x);
        writer.write(_z);
    }

    @Override
    public void deserialize(IByteReader reader)
            throws IOException, ClassNotFoundException, InstantiationException {

        _x = reader.getInteger();
        _z = reader.getInteger();
        seal();
    }

    @Override
    public int hashCode() {
        return _x ^ _z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (obj instanceof ICoords2Di) {
            ICoords2Di other = (ICoords2Di) obj;

            return other.getX() == _x &&
                    other.getZ() == _z;
        }

        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " { x:" + _x + ", z:" + _z + '}';
    }

    /**
     * Set the X coordinate.
     *
     * @param x  The X coordinate.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setX(int x) {
        if (_isImmutable)
            throw new IllegalStateException("Coordinate is immutable.");

        _x = x;
    }

    /**
     * Set the Z coordinate.
     *
     * @param z  The Z coordinate.
     *
     * @throws java.lang.IllegalStateException if the object is immutable.
     */
    protected void setZ(int z) {
        if (_isImmutable)
            throw new IllegalStateException("Coordinate is immutable.");

        _z = z;
    }

    /**
     * Invoked to make the object immutable.
     */
    protected void seal() {
        _isImmutable = true;
    }
}

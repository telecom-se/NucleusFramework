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

package com.jcwhatever.nucleus.storage;

import com.jcwhatever.nucleus.collections.wrappers.AbstractConversionIterator;
import com.jcwhatever.nucleus.regions.data.SyncLocation;
import com.jcwhatever.nucleus.utils.EnumUtils;
import com.jcwhatever.nucleus.utils.LocationUtils;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.items.ItemStackUtils;
import com.jcwhatever.nucleus.utils.items.serializer.InvalidItemStackStringException;
import com.jcwhatever.nucleus.utils.text.TextUtils;

import org.bukkit.Location;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import javax.annotation.Nullable;

/**
 * Abstract implementation of an {@code IDataNode}.
 */
public abstract class AbstractDataNode implements IDataNode {

    private final AbstractDataNode _root;
    protected final String _rawPath;
    protected final String _path;
    protected final String _nodeName;

    // instantiated on root only
    protected final ReadLock _read;
    protected final WriteLock _write;

    /**
     * Constructor for the root node.
     */
    protected AbstractDataNode() {
        _rawPath = "";
        _path = "";
        _nodeName = "";
        _root = this;

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        _read = lock.readLock();
        _write = lock.writeLock();

    }

    /**
     * Constructor for child nodes.
     *
     * @param path  The data node path.
     */
    protected AbstractDataNode(AbstractDataNode root, String path) {

        if (path.endsWith(".")) {
            _rawPath = path.substring(0, path.length() - 1);
            _path = path;
        } else {
            _rawPath = path;
            _path = path + '.';
        }

        String[] pathcomp = TextUtils.PATTERN_DOT.split(_rawPath);
        _nodeName = pathcomp.length > 0
                ? pathcomp[pathcomp.length - 1]
                : "";

        _read = null;
        _write = null;
        _root = root;
    }

    @Override
    public String getName() {
        return _nodeName;
    }

    @Override
    public String getNodePath() {
        return _rawPath;
    }

    @Override
    public boolean getBoolean(String keyPath) {
        return getBoolean(keyPath, false);
    }

    @Override
    public boolean getBoolean(String keyPath, boolean def) {
        Object value = get(keyPath);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        else if (value instanceof String) {
            return TextUtils.parseBoolean((String) value);
        }

        return def;
    }

    @Override
    public int getInteger(String keyPath) {
        return getInteger(keyPath, 0);
    }

    @Override
    public int getInteger(String keyPath, int def) {
        Object value = get(keyPath);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        else if (value instanceof String) {
            return TextUtils.parseInt((String)value, def);
        }

        return def;
    }

    @Override
    public long getLong(String keyPath) {
        return getLong(keyPath, 0);
    }

    @Override
    public long getLong(String keyPath, long def) {
        Object value = get(keyPath);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        else if (value instanceof String) {
            return TextUtils.parseLong((String)value, def);
        }

        return def;
    }

    @Override
    public double getDouble(String keyPath) {
        return getDouble(keyPath, 0.0D);
    }

    @Override
    public double getDouble(String keyPath, double def) {
        Object value = get(keyPath);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        else if (value instanceof String) {
            return TextUtils.parseDouble((String)value, def);
        }

        return def;
    }

    @Nullable
    @Override
    public String getString(String keyPath) {
        return getString(keyPath, null);
    }

    @Nullable
    @Override
    public String getString(String keyPath, @Nullable String def) {
        Object value = get(keyPath);

        if (value instanceof MemorySection)
            return def;

        if (value != null)
            return String.valueOf(value);

        return def;
    }

    @Nullable
    @Override
    public UUID getUUID(String keyPath) {
        return getUUID(keyPath, null);
    }

    @Nullable
    @Override
    public UUID getUUID(String keyPath, @Nullable UUID def) {
        Object value = get(keyPath);
        if (value instanceof UUID) {
            return (UUID) value;
        }
        else if (value instanceof String) {
            UUID result = TextUtils.parseUUID((String)value);
            if (result != null)
                return result;
        }

        return def;
    }

    @Nullable
    @Override
    public SyncLocation getLocation(String keyPath) {
        return getLocation(keyPath, null);
    }

    @Nullable
    @Override
    public SyncLocation getLocation(String keyPath, @Nullable Location def) {
        Object value = get(keyPath);
        if (value instanceof Location) {
            return new SyncLocation((Location) value);
        }
        else if (value instanceof String) {
            SyncLocation location = LocationUtils.parseLocation((String) value);
            if (location != null)
                return location;
        }

        if (def == null)
            return null;

        if (def instanceof SyncLocation)
            return (SyncLocation)def;

        return new SyncLocation(def);
    }

    @Nullable
    @Override
    public ItemStack[] getItemStacks(String keyPath) {
        return getItemStacks(keyPath, (ItemStack[])null);
    }

    @Nullable
    @Override
    public ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack def) {
        return getItemStacks(keyPath, def != null ? new ItemStack[] { def } : null);
    }

    @Nullable
    @Override
    public ItemStack[] getItemStacks(String keyPath, @Nullable ItemStack[] def) {
        Object value = get(keyPath);
        if (value instanceof ItemStack) {
            return new ItemStack[]{(ItemStack) value};
        }
        else if (value instanceof ItemStack[]) {
            return ((ItemStack[])value).clone();
        }
        else if (value instanceof String) {
            try {

                String str = (String)value;

                if (str.indexOf("%ItemStack[]% ") == 0) {
                    str = str.substring(14);
                }

                return ItemStackUtils.parse(str);
            } catch (InvalidItemStackStringException ignore) {}
        }

        return def;
    }

    @Nullable
    @Override
    public <T extends Enum<T>> T getEnum(String keyPath, Class<T> enumClass) {
        return getEnum(keyPath, null, enumClass);
    }

    @Nullable
    @Override
    public <T extends Enum<T>> T getEnum(String keyPath, @Nullable T def, Class<T> enumClass) {
        Object value = get(keyPath);
        if (enumClass.isInstance(value)) {

            @SuppressWarnings("unchecked")
            T result = (T)value;

            return result;
        }
        else if (value instanceof String) {
            T result = EnumUtils.searchEnum((String) value, enumClass);
            if (result != null)
                return result;
        }

        return def;
    }

    @Nullable
    @Override
    public Enum<?> getEnumGeneric(String keyPath, @Nullable Enum<?> def, Class<? extends Enum<?>> enumClass) {
        Object value = get(keyPath);
        if (enumClass.isInstance(value)) {

            @SuppressWarnings("unchecked")
            Enum<?> result = (Enum<?>)value;

            return result;
        }
        else if (value instanceof String) {
            return EnumUtils.searchGenericEnum((String)value, enumClass, def);
        }

        return def;
    }

    @Nullable
    @Override
    public List<String> getStringList(String keyPath, @Nullable List<String> def) {
        Object value = get(keyPath);
        if (value instanceof Collection) {
            Collection collection = (Collection)value;
            List<String> result = new ArrayList<>(collection.size());

            for (Object obj : collection) {
                result.add(String.valueOf(obj));
            }

            return result;
        }
        else if (value instanceof String[]) {

            String[] source = (String[])value;
            List<String> result = new ArrayList<>(source.length);
            Collections.addAll(result, source);

            return result;
        }
        else if (value instanceof String) {
            List<String> result = new ArrayList<>(5);
            result.add(String.valueOf(value));

            return result;
        }

        return def;
    }

    @Nullable
    @Override
    public <T extends IDataNodeSerializable> T getSerializable(String nodePath, Class<T> typeClass) {
        PreCon.notNull(nodePath);
        PreCon.notNull(typeClass);

        if (!hasNode(nodePath))
            return null;

        T instance;

        try {
            Constructor<T> constructor = typeClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            instance = constructor.newInstance();
        } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();

            throw new RuntimeException("Failed to instantiate IDataNodeSerializable object. " +
                    "Make sure it has an empty constructor.");
        }

        IDataNode dataNode = getNode(nodePath);
        _root._read.lock();
        try {
            try {
                instance.deserialize(dataNode);
            } catch (DeserializeException ignore) {
                return null;
            }
        }
        finally {
            _root._read.unlock();
        }

        return instance;
    }

    @Override
    public Iterator<IDataNode> iterator() {
        return new AbstractConversionIterator<IDataNode, String>() {

            Iterator<String> iterator = getSubNodeNames().iterator();

            @Override
            public void remove() {
                _root._write.lock();
                try {
                    iterator.remove();
                }
                finally {
                    _root._write.unlock();
                }
            }

            @Override
            protected IDataNode getElement(String nodeName) {
                return getNode(nodeName);
            }

            @Override
            protected Iterator<String> getIterator() {
                return iterator;
            }
        };
    }

    /**
     * Get the relative path for the data node and
     * get the full path from the root node.
     *
     * @param relativePath  The relative path.
     */
    protected String getFullPath(String relativePath) {
        if (relativePath.isEmpty())
            return _rawPath;

        return _path + relativePath;
    }
}
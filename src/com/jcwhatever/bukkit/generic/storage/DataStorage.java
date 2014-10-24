package com.jcwhatever.bukkit.generic.storage;

import com.jcwhatever.bukkit.generic.utils.PreCon;
import com.jcwhatever.bukkit.generic.utils.TextUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;

public class DataStorage {

    /**
     * Remove data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     *
     * @return  True if successful.
     */
    public static boolean removeStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataStorage.convertStoragePathToFile(plugin, path);
        return file.exists() && file.delete();
    }

    /**
     * Get or create data storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static IDataNode getStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        return new YamlDataStorage(plugin, path);
    }

    /**
     * Get temporary storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static IDataNode getTransientStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        return new YamlDataStorage(plugin, path);
    }

    /**
     * Remove temporary storage.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static boolean removeTransientStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataStorage.convertStoragePathToFile(plugin, path);
        return file.exists() && file.delete();
    }

    /**
     * Determine if a temporary storage exists.
     *
     * @param plugin  The owning plugin.
     * @param path    Storage path.
     */
    public static boolean hasTransientStorage(Plugin plugin, DataPath path) {
        PreCon.notNull(plugin);
        PreCon.notNull(path);

        File file = YamlDataStorage.convertStoragePathToFile(plugin, path);
        return file.exists();
    }

    /**
     * Get a data storage file.
     *
     * @param plugin  The owning plugin.
     * @param file    The file to open.
     */
    public static IDataNode getFileStorage(Plugin plugin, File file) {
        PreCon.notNull(plugin);
        PreCon.notNull(file);

        return new YamlDataStorage(plugin, file);
    }


    public static class DataPath {

        private String[] _dataPath;

        private DataPath() {}

        public DataPath(String path) {
            PreCon.notNullOrEmpty(path);

            _dataPath = TextUtils.PATTERN_DOT.split(path);
        }

        public DataPath(String... paths) {
            PreCon.notNull(paths);
            PreCon.isValid(paths.length > 0);

            _dataPath = paths;
        }

        public DataPath(Collection<String> paths) {
            PreCon.notNull(paths);
            PreCon.isValid(paths.size() > 0);

            _dataPath = paths.toArray(new String[paths.size()]);
        }

        public String[] getPath() {
            return _dataPath;
        }

        public DataPath getPath(String path) {
            PreCon.notNullOrEmpty(path);

            DataPath newPath = new DataPath();
            String[] relativePath = TextUtils.PATTERN_DOT.split(path);

            int size = _dataPath.length + relativePath.length;

            newPath._dataPath = new String[size];

            System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
            System.arraycopy(relativePath, 0, newPath._dataPath, _dataPath.length, relativePath.length);

            return newPath;
        }

        public DataPath getPath(String... paths) {
            PreCon.notNull(paths);
            PreCon.isValid(paths.length > 0);

            DataPath newPath = new DataPath();

            System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
            System.arraycopy(paths, 0, newPath._dataPath, _dataPath.length, paths.length);

            return newPath;
        }

        public DataPath getPath(Collection<String> paths) {
            PreCon.notNull(paths);
            PreCon.isValid(paths.size() > 0);

            DataPath newPath = new DataPath();
            String[] relativePath = paths.toArray(new String[paths.size()]);

            int size = _dataPath.length + relativePath.length;

            newPath._dataPath = new String[size];

            System.arraycopy(_dataPath, 0, newPath._dataPath, 0, _dataPath.length);
            System.arraycopy(relativePath, 0, newPath._dataPath, _dataPath.length, relativePath.length);

            return newPath;
        }

    }

}

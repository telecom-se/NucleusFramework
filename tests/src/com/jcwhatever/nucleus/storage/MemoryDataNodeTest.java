package com.jcwhatever.nucleus.storage;

import com.jcwhatever.bukkit.v1_8_R1.MockPlugin;

public class MemoryDataNodeTest extends IDataNodeTest {

    public MemoryDataNodeTest() {
        setNodeGenerator(new IDataNodeGenerator() {
            @Override
            public IDataNode generateRoot() {
                MockPlugin plugin = new MockPlugin("dummy").enable();

                return new MemoryDataNode(plugin);
            }
        });
    }

}
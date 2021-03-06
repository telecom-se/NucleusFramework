package com.jcwhatever.nucleus.collections.players;

import com.jcwhatever.v1_8_R3.BukkitTester;
import com.jcwhatever.nucleus.collections.java.SetRunnable;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.junit.Test;

import java.util.Collection;

public class PlayerSetTest extends AbstractPlayerCollectionTest {

    protected Plugin _plugin = BukkitTester.mockPlugin("dummy");
    protected Player _player1 = BukkitTester.login("player1");
    protected Player _player2 = BukkitTester.login("player2");
    protected Player _player3 = BukkitTester.login("player3");

    @Test
    public void testSetInterface() throws Exception {

        PlayerSet playerSet = new PlayerSet(_plugin);

        SetRunnable<Player> interfaceTest = new SetRunnable<>(playerSet, _player1, _player2, _player3);
        interfaceTest.run();
    }

    @Override
    protected Collection<Player> getCollection() {
        return new PlayerSet(_plugin);
    }
}
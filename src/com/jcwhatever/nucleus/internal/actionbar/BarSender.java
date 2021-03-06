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

package com.jcwhatever.nucleus.internal.actionbar;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.collections.ElementCounter;
import com.jcwhatever.nucleus.collections.ElementCounter.RemovalPolicy;
import com.jcwhatever.nucleus.collections.SetMap;
import com.jcwhatever.nucleus.collections.WeakHashSetMap;
import com.jcwhatever.nucleus.collections.players.PlayerMap;
import com.jcwhatever.nucleus.collections.timed.TimedDistributor;
import com.jcwhatever.nucleus.internal.NucMsg;
import com.jcwhatever.nucleus.managed.actionbar.ActionBarPriority;
import com.jcwhatever.nucleus.managed.scheduler.Scheduler;
import com.jcwhatever.nucleus.utils.ArrayUtils;
import com.jcwhatever.nucleus.utils.TimeScale;
import com.jcwhatever.nucleus.utils.nms.INmsActionBarHandler;
import com.jcwhatever.nucleus.utils.nms.NmsUtils;
import com.jcwhatever.nucleus.utils.text.dynamic.IDynamicText;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Sends action bars to players and manages {@link PersistentActionBar}'s
 * per player.
 *
 * <p>Also manages packet send timing to reduce network traffic and client side lag
 * based on the max refresh rate required to persist the bar and the minimum
 * refresh rate preferred by the action bars dynamic text. The absolute minimum
 * refresh rate is 1 tick while the absolute max is 40 ticks. The refresh rate is
 * dynamic, meaning it may change due to the dynamic texts refresh rate being dynamic.
 * The refresh rate is managed per each {@link PersistentActionBar} instance.</p>
 */
class BarSender implements Runnable {

    static final int MAX_REFRESH_RATE = 10 * 50;
    static final int MIN_REFRESH_RATE = 50;

    private static final Map<UUID, BarDistributor> PLAYER_MAP = new PlayerMap<>(Nucleus.getPlugin());
    private static final SetMap<ActionBar, PlayerBar> BAR_MAP = new WeakHashSetMap<>(35, 3);
    private static final INmsActionBarHandler NMS_HANDLER;
    static volatile BarSender INSTANCE;

    static {
        NMS_HANDLER = NmsUtils.getActionBarHandler();

        if (NMS_HANDLER == null) {
            NucMsg.debug("Failed to get Action Bar NMS handler.");
        }
    }

    /**
     * Determine if the player is currently viewing any persistent action bar.
     *
     * @param player  The player to check.
     */
    static boolean isViewing(Player player) {
        synchronized (PLAYER_MAP) {
            return PLAYER_MAP.containsKey(player.getUniqueId());
        }
    }

    /**
     * Determine if the player is currently viewing a persistent action bar.
     *
     * @param player     The player to check.
     * @param actionBar  The action bar to check.
     */
    static boolean isViewing(Player player, PersistentActionBar actionBar) {
        BarDistributor distributor;
        synchronized (PLAYER_MAP) {
            distributor = PLAYER_MAP.get(player.getUniqueId());
        }

        if (distributor == null)
            return false;

        synchronized (distributor.sync) {
            return distributor.contains(
                    new PlayerBar(player, actionBar, 0, null, ActionBarPriority.DEFAULT));
        }
    }

    /**
     * Start the sender. Does nothing if already started.
     */
    static void start() {

        if (INSTANCE != null || NMS_HANDLER == null)
            return;

        synchronized (PLAYER_MAP) {

            if (INSTANCE != null)
                return;

            INSTANCE = new BarSender();
            Scheduler.runTaskRepeatAsync(Nucleus.getPlugin(), 1, MIN_REFRESH_RATE / 50, INSTANCE);
        }
    }

    /**
     * Add a {@link PersistentActionBar} to show to a player.
     *
     * @param player     The player who will see the bar.
     * @param actionBar  The action bar to show.
     * @param duration   The duration value. Determines the minimum time slice the bar
     *                   is given when shown with other {@link PersistentActionBar}'s. If the
     *                   action bar is an instance of {@link TimedActionBar}, then duration
     *                   represents the time the bar is displayed before being automatically removed.
     * @param timeScale  The time scale value.
     * @param priority   The action bar priority.
     */
    static void addBar(Player player, PersistentActionBar actionBar,
                       int duration, TimeScale timeScale, ActionBarPriority priority) {

        if (NMS_HANDLER == null)
            return;

        PlayerBar playerBar = new PlayerBar(player, actionBar, duration, timeScale, priority);

        BarDistributor distributor = BarSender.getDistributor(player);

        synchronized (distributor.sync) {

            // ensure distributor does not already contain the playerBar
            if (distributor.contains(playerBar))
                return;

            // add playerBar to distributor
            distributor.add(playerBar, duration, timeScale);
        }

        synchronized (PLAYER_MAP) {
            // add to BAR_MAP
            BAR_MAP.put(actionBar, playerBar);
        }

        if(INSTANCE == null && Bukkit.isPrimaryThread()) {
            start();
        }
    }

    /**
     * Remove a {@link PersistentActionBar} from a player view.
     *
     * @param player     The player.
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(Player player, PersistentActionBar actionBar) {
        if (NMS_HANDLER == null)
            return;

        PlayerBar playerBar = new PlayerBar(player, actionBar, 0,
                TimeScale.TICKS, ActionBarPriority.DEFAULT);

        BarDistributor distributor = BarSender.getDistributor(player);

        boolean isEmpty;

        synchronized (distributor.sync) {
            distributor.remove(playerBar);
            isEmpty = distributor.isEmpty();
        }

        synchronized (PLAYER_MAP) {

            if (isEmpty) {
                PLAYER_MAP.remove(player.getUniqueId());
            }

            BAR_MAP.removeValue(actionBar, playerBar);
        }
    }

    /**
     * Remove a {@link PersistentActionBar} from a player view.
     *
     * @param actionBar  The action bar to remove.
     */
    static void removeBar(PersistentActionBar actionBar) {
        if (NMS_HANDLER == null)
            return;

        Set<PlayerBar> playerBars;

        synchronized (PLAYER_MAP) {
            playerBars = BAR_MAP.removeAll(actionBar);
        }

        for (PlayerBar bar : playerBars) {
            removeBar(bar);
        }
    }

    static <T extends Collection<Player>> T getViewers(PersistentActionBar actionBar, T output) {
        if (NMS_HANDLER == null)
            return output;

        Set<PlayerBar> playerBars;

        synchronized (PLAYER_MAP) {
            playerBars = BAR_MAP.getAll(actionBar);
        }

        if (output instanceof ArrayList)
            ((ArrayList) output).ensureCapacity(playerBars.size());

        for (PlayerBar bar : playerBars) {
            output.add(bar.player());
        }

        return output;
    }

    /**
     * Remove a {@link PlayerBar}.
     *
     * @param bar  The player bar view instance to remove.
     */
    static void removeBar(PlayerBar bar) {
        if (NMS_HANDLER == null)
            return;

        BarDistributor distributor = BarSender.getDistributor(bar.player());
        boolean isEmpty;

        synchronized (distributor.sync) {
            distributor.remove(bar);
            isEmpty = distributor.isEmpty();
        }

        if (isEmpty) {
            synchronized (PLAYER_MAP) {
                PLAYER_MAP.remove(bar.player().getUniqueId());
            }
        }
    }

    /**
     * Remove all action bars from a player.
     *
     * @param player  The player to remove the action bars from.
     */
    static void removePlayer(Player player) {
        if (NMS_HANDLER == null)
            return;

        BarDistributor distributor = PLAYER_MAP.get(player.getUniqueId());
        if (distributor == null)
            return;

        List<PlayerBar> bars = new ArrayList<>(distributor);

        for (PlayerBar bar : bars) {
            removeBar(player, bar.bar());
        }
    }

    /**
     * Get the players current distributor or create a new one.
     *
     * @param player  The player.
     */
    static BarDistributor getDistributor(Player player) {

        BarDistributor distributor;

        synchronized (PLAYER_MAP) {
            distributor = PLAYER_MAP.get(player.getUniqueId());
        }

        if (distributor == null) {

            synchronized (PLAYER_MAP) {
                distributor = new BarDistributor();
                PLAYER_MAP.put(player.getUniqueId(), distributor);
            }
        }

        return distributor;
    }

    /**
     * Get the current priority of the action bar being displayed to
     * a player, if any.
     *
     * @param player  The player.
     *
     * @return  The priority or {@link ActionBarPriority#LOW} if no action bars
     * are being displayed.
     */
    static ActionBarPriority getPriority(Player player) {

        BarDistributor distributor = getDistributor(player);
        if (distributor == null)
            return ActionBarPriority.LOW;

        synchronized (distributor.sync) {
            return distributor.getHighestPriority();
        }
    }

    @Override
    public void run() {

        List<BarDistributor> distributors = new ArrayList<>(10);

        synchronized (PLAYER_MAP){
            if (PLAYER_MAP.isEmpty())
                return;

            // copy distributors to prevent concurrent modification errors.
            distributors.addAll(PLAYER_MAP.values());
        }

        long now = System.currentTimeMillis();
        final List<PlayerBar> toSend = new ArrayList<>(distributors.size() * 2);

        for (BarDistributor distributor : distributors) {

            PlayerBar playerBar;

            synchronized (distributor.sync) {
                // get the current action bar
                playerBar = distributor.current();
                if (playerBar == null)
                    continue;

                while (!distributor.isHighestPriority(playerBar.priority())) {
                    playerBar = distributor.next();
                    if (playerBar == null)
                        throw new AssertionError("Null player bar in bar distributor.");
                }
            }

            // remove expired action bars
            if (playerBar.expires() > 0 && playerBar.expires() <= now) {
                removeBar(playerBar);
                NucMsg.debug("Removing Bar");
                continue;
            }

            // send action bar packet if time to update
            if (playerBar.nextUpdate() == 0 ||
                    playerBar.nextUpdate() <= System.currentTimeMillis()) {

                toSend.add(playerBar);
            }
        }

        if (toSend.isEmpty())
            return;

        Scheduler.runTaskSync(Nucleus.getPlugin(), new Runnable() {
            @Override
            public void run() {
                for (PlayerBar bar : toSend) {
                    bar.send();
                }
            }
        });
    }

    /**
     * Send an action bar packet to a player.
     *
     * @param player     The player.
     * @param actionBar  The action bar to send.
     *
     * @return  The next update time in milliseconds.
     */
    static long send(final Player player, ActionBar actionBar) {
        if (NMS_HANDLER == null)
            return 0;

        IDynamicText dynText = actionBar.getText();
        final CharSequence text = dynText.nextText();
        if (text != null) {
            NMS_HANDLER.send(ArrayUtils.asList(player), text);
        }

        int interval = dynText.getRefreshRate();
        if (interval <= 0) {
            interval = MAX_REFRESH_RATE;
        }
        else {
            interval = Math.min(interval * 50, MAX_REFRESH_RATE);
            interval = Math.max(interval, MIN_REFRESH_RATE);
        }

        return System.currentTimeMillis() + interval;
    }

    static class BarDistributor extends TimedDistributor<PlayerBar> {
        final Object sync = new Object();
        final ElementCounter<ActionBarPriority> priority =
                new ElementCounter<ActionBarPriority>(RemovalPolicy.REMOVE);

        @Override
        public boolean add(@Nonnull PlayerBar element, int timeSpan, TimeScale timeScale) {
            if (super.add(element, timeSpan, timeScale)) {
                priority.add(element.priority());
                return true;
            }
            return false;
        }

        public boolean remove(PlayerBar bar) {
            if (super.remove(bar)) {
                priority.subtract(bar.priority());
                return true;
            }
            return false;
        }

        /**
         * Determine if the specified priority is the highest priority.
         *
         * @param barPriority     The priority to check.
         */
        public boolean isHighestPriority(ActionBarPriority barPriority) {

            switch (barPriority) {
                case HIGH:
                    return true;
                case DEFAULT:
                    return !priority.contains(ActionBarPriority.HIGH);
                case LOW:
                    return !priority.contains(ActionBarPriority.HIGH) &&
                            !priority.contains(ActionBarPriority.DEFAULT);
                default:
                    throw new AssertionError("Unknown ActionBarPriority enum constant: " + barPriority.name());
            }
        }

        public ActionBarPriority getHighestPriority() {
            if (priority.contains(ActionBarPriority.HIGH))
                return ActionBarPriority.HIGH;

            if (priority.contains(ActionBarPriority.DEFAULT))
                return ActionBarPriority.DEFAULT;

            return ActionBarPriority.LOW;
        }
    }
}
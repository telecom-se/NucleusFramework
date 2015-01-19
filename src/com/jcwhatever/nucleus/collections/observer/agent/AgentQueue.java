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

package com.jcwhatever.nucleus.collections.observer.agent;

import com.jcwhatever.nucleus.collections.wrap.QueueWrapper;
import com.jcwhatever.nucleus.mixins.IDisposable;
import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import com.jcwhatever.nucleus.utils.observer.ISubscriberAgent;
import com.jcwhatever.nucleus.utils.observer.Subscriber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

/**
 * A queue of {@link ISubscriberAgent} which automatically removes agents
 * when they are disposed.
 *
 * <p>Assumes the agent is properly implemented and calls the {@code #unregister} method
 * of all {@link com.jcwhatever.nucleus.utils.observer.ISubscriber} instances that are
 * observing it when it's disposed.</p>
 *
 * <p>The collection has its own internal subscriber which is used to observe the agents in
 * the collection. If an implementation wishes to add its own subscriber, it can do so by
 * passing it into the constructor.</p>
 *
 * <p>Implementations may need to use their own synchronization object, in which case it can be
 * passed in via the constructor.</p>
 */
public abstract class AgentQueue<E extends ISubscriberAgent>
        extends QueueWrapper<E> implements IDisposable {

    private final Object _sync;
    private final InternalSubscriber _collectionSubscriber;

    private volatile boolean _isDisposed;

    /**
     * Constructor.
     *
     * <p>Uses a private synchronization object and subscriber.</p>
     */
    public AgentQueue() {
        this(new Object(), null);
    }

    /**
     * Constructor.
     *
     * @param sync        The synchronization object.
     * @param subscriber  The subscriber to use. Optional. A new one is created if null.
     */
    protected AgentQueue(Object sync, @Nullable ISubscriber subscriber) {
        super(sync);

        _sync = sync;
        _collectionSubscriber = new InternalSubscriber(subscriber != null ? subscriber : new Subscriber() {});
    }

    /**
     * Get all subscribers for all agents in the collection..
     */
    public List<ISubscriber> getSubscribers() {

        Collection<E> agents;

        synchronized (_sync) {
            agents = new ArrayList<>(collection());
        }

        List<ISubscriber> result = new ArrayList<>(agents.size() * 3);

        for (ISubscriberAgent agent : agents) {
            result.addAll(agent.getSubscribers());
        }

        return result;
    }

    /**
     * Unregister a subscriber from all agents in the collection.
     *
     * @param subscriber  The subscriber to unregister.
     *
     * @return  True if the subscriber was found in 1 or more producers and removed.
     */
    public boolean unregisterAll(ISubscriber subscriber) {
        PreCon.notNull(subscriber);

        Collection<E> agents;

        synchronized (_sync) {
            agents = new ArrayList<>(collection());
        }

        boolean isChanged = false;
        for (ISubscriberAgent agent : agents) {
            isChanged = agent.unregister(subscriber) || isChanged;
        }

        return isChanged;
    }

    @Override
    public boolean isDisposed() {
        return _isDisposed;
    }

    @Override
    public void dispose() {
        if (_isDisposed)
            return;

        synchronized (_sync) {

            if (_isDisposed)
                return;

            _collectionSubscriber.dispose();
            collection().clear();

            _isDisposed = true;
        }
    }

    @Override
    protected void onAdded(E e) {
        if (isDisposed())
            throw new RuntimeException("Cannot use disposed deque.");

        _collectionSubscriber.register(e);
    }

    @Override
    protected void onRemoved(Object o) {
        _collectionSubscriber.safeUnregister((ISubscriberAgent) o);
    }

    @Override
    protected void onClear(Collection<E> values) {
        for (E agent : values) {
            _collectionSubscriber.safeUnregister(agent);
        }
    }

    private class InternalSubscriber extends CollectionSubscriber {

        InternalSubscriber(ISubscriber subscriber) {
            super(subscriber);
        }

        @Override
        protected void removeFromCollection(ISubscriberAgent agent) {
            synchronized (_sync) {
                //noinspection SuspiciousMethodCalls
                collection().remove(agent);
            }
        }
    }
}

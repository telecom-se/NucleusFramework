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

package com.jcwhatever.nucleus.utils.observer.event;

import com.jcwhatever.nucleus.utils.observer.ISubscriber;
import javax.annotation.Nullable;

/**
 * A type that subscribes to events received from an {@link IEventAgent}.
 *
 * <p>Usage is similar in context to an update subscriber except the event subscriber
 * is more complex.</p>
 *
 * <p>If you're usage context does not require event priorities and event cancelling,
 * it is recommended to use {@link com.jcwhatever.nucleus.utils.observer.update.IUpdateSubscriber}
 * in conjunction with {@link com.jcwhatever.nucleus.utils.observer.update.IUpdateAgent}
 * instead.</p>
 */
public interface IEventSubscriber<E> extends ISubscriber, Comparable<IEventSubscriber<E>> {

    /**
     * Get the event priority.
     */
    EventSubscriberPriority getPriority();

    /**
     * Determine if the subscriber should still be notified
     * of an event if it's cancelled.
     */
    boolean isInvokedForCancelled();

    /**
     * Invoked by an {@link IEventAgent} to notify the subscriber
     * of an event.
     *
     * @param source  The source of the event.
     * @param event   The event.
     */
    void onEvent(@Nullable Object source, E event);
}

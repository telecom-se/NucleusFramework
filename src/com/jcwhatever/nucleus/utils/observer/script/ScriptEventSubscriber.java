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

package com.jcwhatever.nucleus.utils.observer.script;

import com.jcwhatever.nucleus.utils.PreCon;
import com.jcwhatever.nucleus.utils.observer.event.EventSubscriber;

import javax.annotation.Nullable;

/**
 * Used to pass in an {@link com.jcwhatever.nucleus.utils.observer.event.IEventSubscriber}
 * from a script using {@link IScriptEventSubscriber}.
 *
 * <p>An {@link IScriptEventSubscriber} can be passed in from a script and encapsulated by
 * {@link ScriptEventSubscriber} to convert it to an {@link EventSubscriber}.</p>
 */
public class ScriptEventSubscriber<E> extends EventSubscriber<E> {

    private final IScriptEventSubscriber<E> _scriptSubscriber;

    /**
     * Constructor.
     *
     * @param subscriber  The subscriber passed in from a script.
     */
    public ScriptEventSubscriber(IScriptEventSubscriber<E> subscriber) {
        PreCon.notNull(subscriber);

        _scriptSubscriber = subscriber;
    }

    @Override
    public void onEvent(@Nullable Object caller, E event) {
        _scriptSubscriber.onEvent(event, this);
    }
}

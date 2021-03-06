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

package com.jcwhatever.nucleus.utils.nms;

import com.jcwhatever.nucleus.managed.actionbar.ActionBars;
import com.jcwhatever.nucleus.managed.actionbar.IActionBar;
import com.jcwhatever.nucleus.managed.actionbar.IPersistentActionBar;
import com.jcwhatever.nucleus.managed.actionbar.ITimedActionBar;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Interface for NucleusFramework's Minecraft Action Bar handler.
 *
 * @see NmsUtils
 * @see ActionBars
 * @see IActionBar
 * @see IPersistentActionBar
 * @see ITimedActionBar
 */
public interface INmsActionBarHandler extends INmsHandler {

    /**
     * Send action bar text to a collection of players.
     *
     * <p>The handler is responsible for converting the raw text
     * to whatever format is required.</p>
     *
     * @param players  The players to send the text to.
     * @param rawText  The raw text.
     */
    void send(Collection<? extends Player> players, CharSequence rawText);

    /**
     * Send action bar text to a collection of players.
     *
     * <p>Bypasses the handlers text conversion.</p>
     *
     * @param players   The player to send the text to.
     * @param jsonText  The Json text.
     */
    void sendJson(Collection<? extends Player> players, CharSequence jsonText);
}

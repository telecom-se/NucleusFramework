/*
 * This file is part of GenericsLib for Bukkit, licensed under the MIT License (MIT).
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

package com.jcwhatever.bukkit.generic.internal;

import com.jcwhatever.bukkit.generic.GenericsLib;
import com.jcwhatever.bukkit.generic.scripting.ScriptApiRepo;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiDepends;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiEconomy;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiEvents;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiInventory;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiItemBank;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiJail;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiMsg;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiPermissions;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiRand;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiScheduler;
import com.jcwhatever.bukkit.generic.scripting.api.ScriptApiSounds;

/**
 * GenericsLib's internal script api repository
 */
public final class InternalScriptApiRepo extends ScriptApiRepo {

    /**
     * Private Constructor.
     */
    public InternalScriptApiRepo() {
        super();

        registerApiType(GenericsLib.getPlugin(), ScriptApiEvents.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiEconomy.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiInventory.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiItemBank.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiJail.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiMsg.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiPermissions.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiSounds.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiDepends.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiRand.class);
        registerApiType(GenericsLib.getPlugin(), ScriptApiScheduler.class);
    }
}

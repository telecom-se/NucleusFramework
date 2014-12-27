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

package com.jcwhatever.nucleus.internal.commands.titles;

import com.jcwhatever.nucleus.Nucleus;
import com.jcwhatever.nucleus.commands.AbstractCommand;
import com.jcwhatever.nucleus.commands.CommandInfo;
import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.internal.Lang;
import com.jcwhatever.nucleus.language.Localizable;

import org.bukkit.command.CommandSender;

@CommandInfo(
        parent="titles",
        command="del",
        staticParams={ "titleName" },
        description="Remove a title.",

        paramDescriptions = {
                "titleName= The name of the title to remove."})

public final class DelSubCommand extends AbstractCommand {

    @Localizable static final String _FAILED = "A title named '{0}' was not found.";
    @Localizable static final String _SUCCESS = "Title named '{0}' removed.";

    @Override
    public void execute(CommandSender sender, CommandArguments args) throws CommandException {

        String name = args.getName("titleName", 32);

        if (Nucleus.getTitleManager().removeTitle(name) == null) {
            tellError(sender, Lang.get(_FAILED, name));
        }

        tellSuccess(sender, Lang.get(_SUCCESS, name));
    }
}

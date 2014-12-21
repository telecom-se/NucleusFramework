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

package com.jcwhatever.bukkit.generic.commands;

import com.jcwhatever.bukkit.generic.commands.parameters.CommandParameter;
import com.jcwhatever.bukkit.generic.commands.parameters.FlagParameter;
import com.jcwhatever.bukkit.generic.internal.Lang;

import java.util.LinkedList;
import java.util.List;

/*
 * 
 */
public class UsageGenerator {

    private static final String _HELP_USAGE = "/{0: root command}{1: command path}{2: command}{3: parameters}";
    private static final String _HELP_USAGE_HAS_SUB_COMMANDS = "{GOLD}/{0: root command}{GREEN}{1: command path}{2: command}?";

    public UsageGenerator() {

    }

    public String generate(AbstractCommand command) {

        String rootCommandName = command.getInfo().getRootSessionName();
        if (rootCommandName == null) {
            rootCommandName = command.getInfo().getSessionName();
        }

        return command.getCommandCollection().size() == 0
                ? generate(command, rootCommandName, _HELP_USAGE)
                : generate(command, rootCommandName, _HELP_USAGE_HAS_SUB_COMMANDS);
    }

    public String generate(AbstractCommand command, String rootCommandName) {

        return command.getCommandCollection().size() == 0
                ? generate(command, rootCommandName, _HELP_USAGE)
                : generate(command, rootCommandName, _HELP_USAGE_HAS_SUB_COMMANDS);
    }

    public String generate(AbstractCommand command, String rootCommandName, String template) {

        LinkedList<AbstractCommand> parentCommands = new LinkedList<>();
        StringBuilder commandPath = new StringBuilder(30);

        if (command.getParent() != null) {
            AbstractCommand parent = command;

            while ((parent = parent.getParent()) != null && parent.getParent() != null) {
                if (command != parent) {
                    parentCommands.push(parent);
                }
            }

            while(!parentCommands.isEmpty()) {
                commandPath.append(parentCommands.pop().getInfo().getName());
                commandPath.append(' ');
            }
        }

        StringBuilder params = new StringBuilder(30);

        List<CommandParameter> staticParams = command.getInfo().getStaticParams();
        for (CommandParameter parameter : staticParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append(parameter.getName())
                    .append(isRequired ? '>' : ']')
                    .append(' ');
        }

        List<CommandParameter> floatingParams = command.getInfo().getFloatingParams();
        for (CommandParameter parameter : floatingParams) {

            boolean isRequired = !parameter.hasDefaultValue();

            params.append(isRequired ? '<' : '[')
                  .append("--")
                  .append(parameter.getName())
                  .append(isRequired ? '>' : ']')
                    .append(' ');
        }

        List<FlagParameter> flagParams = command.getInfo().getFlagParams();
        for (FlagParameter parameter : flagParams) {
            params.append('[')
                  .append('-')
                  .append(parameter.getName())
                    .append("] ");
        }

        return Lang.get(command.getPlugin(),
                template, rootCommandName + ' ', commandPath, command.getInfo().getName() + ' ', params);
    }

}

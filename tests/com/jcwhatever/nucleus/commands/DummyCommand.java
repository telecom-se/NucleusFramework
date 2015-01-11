package com.jcwhatever.nucleus.commands;

import com.jcwhatever.nucleus.commands.arguments.CommandArguments;
import com.jcwhatever.nucleus.commands.exceptions.CommandException;
import com.jcwhatever.nucleus.utils.ArrayUtils;

import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.lang.annotation.Annotation;

@CommandInfo(
        command="dummy",
        description="Dummy command")
public class DummyCommand extends AbstractCommand {

    private DummyCommandExecutable _executable;
    private CommandInfoContainer _info;

    public void setExecutable(DummyCommandExecutable executable) {
        _executable = executable;
    }

    @Override
    public void execute(CommandSender sender, CommandArguments args)
            throws CommandException {

        if (_executable != null)
            _executable.execute(sender, args);
    }

    public interface DummyCommandExecutable {

        public void execute(CommandSender sender, CommandArguments args)
                throws CommandException;
    }

    @Override
    public CommandInfoContainer getInfo() {
        if (_info == null && getDispatcher() != null)
            _info = new CommandInfoContainer(this, null);

        return _info;
    }

    public void setInfo(CommandInfo commandInfo) {
        _info = new CommandInfoContainer(this, null, commandInfo);
    }

    public static CommandInfo createInfoAnnotation(final String parent,
                                                   final String name,
                                                   final String[] staticParams,
                                                   final String[] floatingParams,
                                                   final String[] flags,
                                                   final String[] paramDescriptions,
                                                   final String usage,
                                                   final String description,
                                                   final String longDescription,
                                                   final boolean isHelpVisible,
                                                   final PermissionDefault permissionDefault) {

        return new CommandInfo() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return CommandInfo.class;
            }

            @Override
            public String parent() {
                return parent;
            }

            @Override
            public String[] command() {
                return new String[] { name };
            }

            @Override
            public String[] staticParams() {
                return staticParams;
            }

            @Override
            public String[] floatingParams() {
                return floatingParams;
            }

            @Override
            public String[] flags() {
                return flags;
            }

            @Override
            public String[] paramDescriptions() {
                return paramDescriptions;
            }

            @Override
            public String usage() {
                return usage;
            }

            @Override
            public String description() {
                return description;
            }

            @Override
            public String longDescription() {
                return longDescription;
            }

            @Override
            public boolean isHelpVisible() {
                return isHelpVisible;
            }

            @Override
            public PermissionDefault permissionDefault() {
                return permissionDefault;
            }
        };
    }


    public static class CommandInfoBuilder {

        final String name;
        String parent = "";
        String[] staticParams = ArrayUtils.EMPTY_STRING_ARRAY;
        String[] floatingParams = ArrayUtils.EMPTY_STRING_ARRAY;
        String[] flags = ArrayUtils.EMPTY_STRING_ARRAY;
        String[] paramDescriptions = ArrayUtils.EMPTY_STRING_ARRAY;
        String usage = "";
        String description = "";
        String longDescription = "";
        boolean isHelpVisible = true;
        PermissionDefault permissionDefault = PermissionDefault.OP;

        public CommandInfoBuilder(String commandName) {
            name = commandName;
        }

        public CommandInfoBuilder parent(String name) {
            parent = name;
            return this;
        }

        public CommandInfoBuilder staticParams(String... params) {
            staticParams = params;
            return this;
        }

        public CommandInfoBuilder floatingParams(String... params) {
            floatingParams = params;
            return this;
        }

        public CommandInfoBuilder flags(String... names) {
            flags = names;
            return this;
        }

        public CommandInfoBuilder paramDescriptions(String... descriptions) {
            paramDescriptions = descriptions;
            return this;
        }

        public CommandInfoBuilder usage(String usage) {
            this.usage = usage;
            return this;
        }

        public CommandInfoBuilder description(String description) {
            this.description = description;
            return this;
        }

        public CommandInfoBuilder longDescription(String description) {
            this.longDescription = description;
            return this;
        }

        public CommandInfoBuilder helpVisible(boolean isHelpVisible) {
            this.isHelpVisible = isHelpVisible;
            return this;
        }

        public CommandInfoBuilder permissionDefault(PermissionDefault permissionDefault) {
            this.permissionDefault = permissionDefault;
            return this;
        }

        public CommandInfo build() {
            return createInfoAnnotation(parent,
                    name,
                    staticParams,
                    floatingParams,
                    flags,
                    paramDescriptions,
                    usage,
                    description,
                    longDescription,
                    isHelpVisible,
                    permissionDefault);
        }
    }

}

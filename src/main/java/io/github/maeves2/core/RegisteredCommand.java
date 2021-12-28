package io.github.maeves2.core;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Class which holds a registered slash command, destined for internal use.
 * Commands which are registered using auto-registering or
 * {@link io.github.maeves2.SnowballNebula#register(Class[])} are stored using
 * this class.
 * @since 1.0.0
 * @author Maeves2
 */
public class RegisteredCommand {
    /**
     * Instance of the command, holds the class to which belongs the method
     * annotated with {@link Slash}. Used to invoke said method when
     * the command is executed.
     */
    private Object instance;
    /**
     * Name of the command
     */
    private String name;
    /**
     * Permission needed to execute the slash command
     */
    private Permission perm;
    /**
     * The message sent when a user who doesn't have the needed permission
     * executes the command
     */
    private String permissionMessage;
    /**
     * Method which handles command execution. This method is invoked on
     * {@code instance} when the slash command is executed.
     */
    private Method method;
    /**
     * Command data of the command
     */
    private CommandData data;
    /**
     * A list of all options
     */
    private List<OptionData> options;
    /**
     * Is the command global command
     */
    private boolean globalCommand;

    public RegisteredCommand(Object instance,
                             String name,
                             Permission perm,
                             String permissionMessage,
                             Method method,
                             CommandData data,
                             List<OptionData> options,
                             boolean globalCommand) {
        this.instance = instance;
        this.name = name;
        this.perm = perm;
        this.permissionMessage = permissionMessage;
        this.method = method;
        this.data = data;
        this.options = options;
        this.globalCommand = globalCommand;
    }

    public Object getInstance() {
        return instance;
    }

    public String getName() {
        return name;
    }

    public Permission getPerm() {
        return perm;
    }

    public String getPermissionMessage() {
        return permissionMessage;
    }

    public Method getMethod() {
        return method;
    }

    public CommandData getData() {
        return data;
    }

    public List<OptionData> getOptions() {
        return options;
    }

    public boolean isGlobalCommand() {
        return globalCommand;
    }
}

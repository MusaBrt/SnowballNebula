package io.github.maeves2;

import io.github.maeves2.core.AutoRegister;
import io.github.maeves2.core.RegisteredCommand;
import io.github.maeves2.core.Slash;
import io.github.maeves2.util.Utilities;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main class of the SnowballNebula framework. Used to register commands,
 * enable auto registering and get registered commands as {@link RegisteredCommand}.
 * <h2>Roadmap</h2>
 * <li>Command aliases (SoonTM)</li>
 * <li>Automatically registering commands in guilds, global commands (SoonTM)</li>
 * <li>Databases (NeverTM)</li>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * var snowball = new SnowballNebula(jda)
 *                 .enableLogging()
 *                 .enableAutoRegistering(Testing.class.getPackageName());
 * var commands = snowball.getCommands();
 * jda.getGuilds().forEach(e -> e.updateCommands().addCommands(commands).queue());
 * }</pre>
 *
 * @since 1.0.0
 * @author MaeveS2
 */
public class SnowballNebula {
    /**
     * Your JDA instance
     */
    private JDA jda;
    /**
     * Map containing all commands that were registred using {@link SnowballNebula#register(Class[])}
     * or auto-registering. Can be retrieved using {@link SnowballNebula#getCommand(String)}
     * or {@link SnowballNebula#getCommands()}
     */
    private Map<String, RegisteredCommand> registry;

    /**
     * Should certain events events be logged? Connection to a bot is always logged.
     */
    private boolean log = false;

    /**
     * @return the log field
     */
    public boolean isLogging() {
        return log;
    }

    /**
     * Logger for all the SnowballNebula things.
     */
    static final Logger logger = LoggerFactory.getLogger(SnowballNebula.class);

    /**
     * EventHorizon instance for further usages.
     */
    private final EventHorizon eventHorizon;

    /**
     * Constructor of this class :bruh: <br>
     * <strong>Please make sure to use {@link JDA#awaitReady()} before
     * creating an instance of this class!!</strong>
     * @param jda Your JDA instance
     */
    public SnowballNebula(JDA jda) {
        this.eventHorizon = new EventHorizon(this);
        jda.addEventListener(this.eventHorizon);
        this.jda = jda;
        logger.info("Successfully connected to bot " + jda.getSelfUser().getName());
        this.registry = new HashMap<>();
    }

    /**
     * Enable logging for the framework. If logging is enabled, the following
     * will be logged:
     * <li>Registering of commands</li>
     * <li>Execution of commands</li>
     * <li>Enabling of auto-registering</li>
     * @return {@link SnowballNebula} for chaining convenience
     */
    public SnowballNebula enableLogging() {
        this.log = true;
        return this;
    }

    /**
     * Returns the specified JDA instance, destined for internal use.
     * @return {@link JDA}
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Scans one or more classes for methods annotated with {@link Slash}
     * and registers them.
     * @param classes The class or classes that should be checked for commands
     * @return {@link SnowballNebula} for chaining convenience
     */
    public SnowballNebula register(Class<?>... classes) {
        for (var clazz : classes) {
            for (var method : clazz.getMethods()) {
                var command = method.getAnnotation(Slash.class);
                if (command != null) {
                    var options = Utilities.asOptionData(command.options());
                    var data = new CommandData(command.name(), command.desc()).addOptions(options);
                    registry.putIfAbsent(command.name(), new RegisteredCommand(
                            clazz, command.name(), command.perm(), command.permissionMessage(),
                            method, data, options, command.globalCommand()));
                    if (log) logger.info("Registered new command (/" + command.name() + ") from " + clazz.getName());
                }
            }
        }
        return this;
    }

    /**
     * Enables auto-registering for a specified package. Classes that should
     * automatically be registered are marked with the marker annotation
     * {@link AutoRegister}.
     * @param targetPackage The package in which auto-registering will be
     *        enabled. Can be obtained using {@link Class#getPackageName()}.
     * @return {@link SnowballNebula} for chaining convenience
     */
    public SnowballNebula enableAutoRegistering(String targetPackage) {
        Utilities.getAllClassesFromPackage(targetPackage).stream()
                .filter(e -> e.isAnnotationPresent(AutoRegister.class))
                .collect(Collectors.toSet())
                .forEach(this::register);
        if (log) logger.info("Enabled automatic registring");
        return this;
    }

    /**
     * Uploads the commands marked as globalCommand to Discord.
     * @return {@link SnowballNebula} for chaining convenience
     */
    public SnowballNebula upsertGlobalCommands() {
        if (registry.isEmpty()) {
            if (log) logger.warn("You should call the SnowballNebula#enableAutoRegistring(String) method before calling the SnowballNebula#upsertGlobalCommands().");
            return this;
        }
        int counter = 0;
        for (RegisteredCommand value : registry.values()) {
            if (value.isGlobalCommand()) {
                jda.upsertCommand(value.getData()).addOptions(value.getOptions()).queue();
                counter++;
            }
        }
        if (log) logger.info(String.format("%d global commands upserted to Discord succesfully.", counter));
        return this;
    }

    /**
     * Returns a registered command, destined for internal use.
     * @param name Name of the command
     * @return {@link RegisteredCommand}
     */
    public RegisteredCommand getRegisteredCommand(String name) {
        return registry.get(name);
    }

    /**
     * Returns the command data of a command by name.
     * @param name The name of the command
     * @return {@link CommandData}
     */
    public CommandData getCommand(String name) {
        return registry.get(name).getData();
    }

    /**
     * Returns a list of all command (command data)
     * @return {@link List}
     */
    public List<CommandData> getCommands() {
        return registry.values().stream().map(RegisteredCommand::getData).toList();
    }
}

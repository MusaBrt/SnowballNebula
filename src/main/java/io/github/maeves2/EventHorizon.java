package io.github.maeves2;

import io.github.maeves2.util.Utilities;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Class that handles the slash command events from Discord.
 * @since 1.0.0
 * @author Maeves2
 * @author Koply
 */
public class EventHorizon extends ListenerAdapter {

    /**
     * SnowballNebula instance for use the {@link io.github.maeves2.SnowballNebula#getRegisteredCommand(String)}
     * method in the {@link EventHorizon#onSlashCommand(SlashCommandEvent)}.
     */
    private final SnowballNebula snowballNebula;

    /**
     * are we logging events?
     */
    private final boolean log;

    /**
     * Constructor for the Listener class of the SnowballNebula.
     * @param snowballNebula instance for us.
     */
    public EventHorizon(SnowballNebula snowballNebula) {
        this.snowballNebula = snowballNebula;
        this.log = snowballNebula.isLogging();
    }

    /**
     * Code to execute when a slash command is used. Overridden from the
     * {@link ListenerAdapter} class.
     * <h2>Do not use this method!!</h2>
     */
    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        if (event.getUser().equals(event.getJDA().getSelfUser())) return;
        var command = snowballNebula.getRegisteredCommand(event.getName());
        Objects.requireNonNull(command);

        if (event.isFromGuild() && event.getMember() == null) return;

        var perm = command.getPerm();
        if (!event.getMember().hasPermission(perm)) {
            var message = command.getPermissionMessage().replace("$PERMISSION$",
                    "`" + perm.getName() + "`");
            event.replyEmbeds(Utilities.failEmbed(message)).queue();
            return;
        }
        try {
            command.getMethod().invoke(command.getInstance(), event);
            if (log) SnowballNebula.logger.info(event.getUser().getAsTag() + " executed command /" +
                    event.getName() + " in " + event.getGuild());
        } catch (Exception e) {
            event.replyEmbeds(Utilities.failEmbed("Sorry, something went wrong...")).queue();
            e.printStackTrace();
        }
    }
}
package io.github.maeves2;

import io.github.maeves2.core.AutoRegister;
import io.github.maeves2.core.Option;
import io.github.maeves2.core.Slash;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

@AutoRegister
public class CommandTest {

    @Slash(name = "say", desc = "Make the bot say something", perm = Permission.UNKNOWN, options = {
            @Option(type = OptionType.STRING, name = "text", desc = "Text to say", required = true),
            @Option(type = OptionType.BOOLEAN, name = "embed", desc = "Make it an embed?")
    })
    public static void say(SlashCommandEvent event) {
        var text = event.getOption("text").getAsString();
        event.reply(text).queue();
    }
}

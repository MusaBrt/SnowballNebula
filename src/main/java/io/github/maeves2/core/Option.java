package io.github.maeves2.core;

import net.dv8tion.jda.api.interactions.commands.OptionType;

/**
 * Annotation used to create an option for a slash command.
 */
public @interface Option {
    /**
     * Type of the option, specified using the {@link OptionType} enum.
     * @return Type of the option, for example {@code OptionType.STRING}
     */
    OptionType type();

    /**
     * The name of the option
     * @return The name of the option
     */
    String name();

    /**
     * The description of the option, defaults to a blank string.
     * @return The description of the option
     */
    String desc() default "";

    /**
     * Whether the option is required or not, defaults to {@code false}.
     * @return Whether the option is required
     */
    boolean required() default false;
}

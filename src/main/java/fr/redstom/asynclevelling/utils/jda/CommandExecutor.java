package fr.redstom.asynclevelling.utils.jda;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public interface CommandExecutor {

    SlashCommandData data();

    void execute(SlashCommandInteractionEvent event);

    default void autocomplete(CommandAutoCompleteInteractionEvent event) {}
}

package fr.redstom.asynclevelling.events;

import fr.redstom.asynclevelling.utils.jda.CommandExecutor;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SlashCommandListener extends ListenerAdapter {

    private final List<CommandExecutor> executors;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.isFromGuild()) return;

        executors.stream()
                .filter(ex -> ex.data().getName().equalsIgnoreCase(event.getName()))
                .findFirst()
                .ifPresentOrElse(
                        ex -> ex.execute(event),
                        () ->
                                event.reply(
                                                ":x: Impossible de trouver un exécuteur pour cette"
                                                        + " commande !")
                                        .queue());
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (!event.isFromGuild()) return;

        executors.stream()
                .filter(ex -> ex.data().getName().equalsIgnoreCase(event.getName()))
                .findFirst()
                .ifPresentOrElse(
                        ex -> ex.autocomplete(event), () -> event.replyChoices(List.of()).queue());
    }
}

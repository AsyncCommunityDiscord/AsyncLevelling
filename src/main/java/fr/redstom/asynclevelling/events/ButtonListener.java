package fr.redstom.asynclevelling.events;

import fr.redstom.asynclevelling.utils.jda.ButtonExecutor;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ButtonListener extends ListenerAdapter {

    private final List<ButtonExecutor> buttonExecutors;

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        buttonExecutors.stream()
                .filter(be -> be.id().equalsIgnoreCase(event.getButton().getId().split(";")[0]))
                .findFirst()
                .ifPresentOrElse(
                        be -> {
                            String[] args = event.getButton().getId().split(";");
                            args = Arrays.copyOfRange(args, 1, args.length);

                            be.execute(event, args);
                        },
                        () ->
                                event.reply(
                                                ":x: Impossible de trouver un exécuteur pour ce"
                                                        + " bouton !")
                                        .queue());
    }
}

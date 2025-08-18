package fr.redstom.asynclevelling.buttons;

import fr.redstom.asynclevelling.utils.jda.ButtonExecutor;
import fr.redstom.asynclevelling.utils.jda.Command;

import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;

@Command
public class DeleteButton implements ButtonExecutor {

    public static final Button DELETE_BUTTON =
            Button.of(ButtonStyle.DANGER, "delete", Emoji.fromUnicode("🗑️"));

    @Override
    public String id() {
        return "delete";
    }

    @Override
    public void execute(ButtonInteractionEvent event, String[] args) {
        event.deferReply()
                .queue(
                        (hook) ->
                                event.getMessage()
                                        .delete()
                                        .queue(_ -> hook.deleteOriginal().queue()));
    }
}

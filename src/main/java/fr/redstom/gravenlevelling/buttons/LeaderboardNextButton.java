package fr.redstom.gravenlevelling.buttons;

import fr.redstom.gravenlevelling.jpa.services.GravenGuildService;
import fr.redstom.gravenlevelling.utils.ButtonExecutor;
import fr.redstom.gravenlevelling.utils.Command;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.utils.FileUpload;

@Command
@RequiredArgsConstructor
public class LeaderboardNextButton implements ButtonExecutor {

    private final GravenGuildService guildService;

    @Override
    public String id() {
        return "lb-next";
    }

    @Override
    public void execute(ButtonInteractionEvent event, String[] args) {
        int page = Integer.parseInt(args[0]) + 1;
        InteractionHook hook = event.deferReply(true).complete();

        byte[] data = guildService.getLeaderboardImageFor(event.getGuild(), page, event.getMember());
        if(data == null) {
            hook.sendMessage(STR.":x: Il n'existe pas pas de page n°**\{page}** !").setEphemeral(true).queue();
            return;
        }

        hook.editOriginalAttachments(FileUpload.fromData(data, "image.png"))
                .setContent("")
                .setActionRow(
                        Button.of(ButtonStyle.PRIMARY, STR."lb-previous;\{page}", "Précédent", Emoji.fromUnicode("⬅\uFE0F"))
                                .withDisabled(page == 1),
                        Button.of(ButtonStyle.SUCCESS, "euuuuuuuh", STR."Page \{page}").asDisabled(),
                        Button.of(ButtonStyle.PRIMARY, STR."lb-next;\{page}", "Suivant", Emoji.fromUnicode("➡\uFE0F"))
                )
                .queue();
    }
}

package fr.redstom.asynclevelling.commands;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.redstom.asynclevelling.jpa.services.GuildSettingsService;
import fr.redstom.asynclevelling.jpa.services.MemberService;
import fr.redstom.asynclevelling.utils.imports.ImportEntry;
import fr.redstom.asynclevelling.utils.jda.Command;
import fr.redstom.asynclevelling.utils.jda.CommandExecutor;
import fr.redstom.asynclevelling.utils.jda.EmbedUtils;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.io.InputStream;
import java.util.List;

@Command
@RequiredArgsConstructor
@Slf4j
public class CommandImport implements CommandExecutor {

    private final GuildSettingsService guildSettingsService;
    private final ObjectMapper mapper;
    private final MemberService memberService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("import", "Importer des niveaux depuis des données existantes")
                .addSubcommands(
                        new SubcommandData("json", "Importer des niveaux depuis un fichier JSON")
                                .addOption(
                                        OptionType.ATTACHMENT,
                                        "file",
                                        "Fichier JSON à importer dans le format [{ id: long, level:"
                                                + " long }]",
                                        true),
                        new SubcommandData(
                                        "roles",
                                        "Active ou désactive l'importation depuis des rôles"
                                                + " Discord")
                                .addOption(
                                        OptionType.BOOLEAN,
                                        "enable",
                                        "Activer ou désactiver l'importation depuis des rôles"
                                                + " Discord",
                                        true))
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "json" -> importFromJson(event);
            case "roles" -> importFromRoles(event);
        }
    }

    public void importFromJson(SlashCommandInteractionEvent event) {
        Message.Attachment file = event.getOption("file").getAsAttachment();

        if (!"json".equals(file.getFileExtension())) {
            event.replyEmbeds(EmbedUtils.error("Le fichier doit être au format JSON !").build())
                    .queue();
            return;
        }

        InteractionHook hook = event.deferReply().complete();
        file.getProxy().download().thenAccept(stream -> this.decode(event, hook, stream));

        log.info(
                "{} imported a json file for guild \"{}\"",
                event.getMember().getUser().getAsTag(),
                event.getGuild().getName());
    }

    @SneakyThrows
    private void decode(
            SlashCommandInteractionEvent event, InteractionHook hook, InputStream stream) {
        List<ImportEntry> model;
        try {
            model = mapper.readValue(stream, new TypeReference<>() {});
        } catch (Exception e) {
            hook.editOriginal("")
                    .setEmbeds(
                            EmbedUtils.error(
                                            "Erreur lors de la lecture du fichier : "
                                                    + e.getMessage())
                                    .build())
                    .queue();
            return;
        }
        hook.editOriginal("🕛 Importation des niveaux en cours...").complete();

        Guild guild = event.getGuild();
        for (ImportEntry entry : model) {
            memberService.getMemberByGuildAndMemberId(guild, entry.id(), entry.level());
        }
        hook.editOriginal("✅ Importation des niveaux terminée !").complete();
    }

    public void importFromRoles(SlashCommandInteractionEvent event) {
        boolean enable = event.getOption("enable").getAsBoolean();

        guildSettingsService.applyAndSave(
                event.getGuild(), settings -> settings.toBuilder().autoLevelGrant(enable).build());

        event.reply(
                        "✅ L'importation depuis des rôles Discord a été "
                                + (enable ? "activée" : "désactivée")
                                + " !")
                .queue();

        log.info(
                "{} {} the importation of levels by role in guild \"{}\"",
                event.getMember().getUser().getAsTag(),
                enable ? "enabled" : "disabled",
                event.getGuild().getName());
    }
}

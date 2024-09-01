package fr.redstom.gravenlevelling.commands;

import fr.redstom.gravenlevelling.jpa.services.GravenGuildSettingsService;
import fr.redstom.gravenlevelling.utils.jda.Command;
import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;
import fr.redstom.gravenlevelling.utils.jda.ModalHandler;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.channel.unions.GuildChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

@Command
@RequiredArgsConstructor
public class CommandSettings implements CommandExecutor {


    private final GravenGuildSettingsService settingsService;

    @Override
    public SlashCommandData data() {
        return Commands.slash("settings", "Configure the bot settings")
                .addSubcommands(
                        new SubcommandData("set-notification-channel", "Définit le salon où les notifications de montée de niveau seront envoyées")
                                .addOption(OptionType.CHANNEL, "channel", "Le salon où envoyer les notifications", true),
                        new SubcommandData("set-dm-notifications", "Active ou désactive les notifications par message privé")
                                .addOption(OptionType.BOOLEAN, "enabled", "Active ou désactive les notifications par message privé", true),
                        new SubcommandData("set-level-notifications", "Active ou désactive les notifications de montée de niveau")
                                .addOption(OptionType.BOOLEAN, "enabled", "Active ou désactive les notifications de montée de niveau", true),
                        new SubcommandData("set-reward-notifications", "Active ou désactive les notifications de récompense")
                                .addOption(OptionType.BOOLEAN, "enabled", "Active ou désactive les notifications de récompense", true),
                        new SubcommandData("set-notification-message", "Définit le message de notification de montée de niveau"),
                        new SubcommandData("set-reward-notification-message", "Définit le message de notification de récompense"),
                        new SubcommandData("get-placeholders", "Renvoie la liste des placeholders utilisables dans les messages de notification")
                )
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "set-notification-channel" -> setNotificationChannel(event);
            case "set-dm-notifications" -> setDmNotifications(event);
            case "set-level-notifications" -> setLevelNotifications(event);
            case "set-reward-notifications" -> setRewardNotifications(event);
            case "set-notification-message" -> setNotificationMessage(event);
            case "set-reward-notification-message" -> setRewardNotificationMessage(event);
            case "get-placeholders" -> sendPlaceholders(event);
        }
    }

    private void setNotificationChannel(SlashCommandInteractionEvent event) {
        GuildChannelUnion channel = event.getOption("channel").getAsChannel();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder()
                .notificationChannelId(channel.getIdLong())
                .notificationChannelType(channel.getType())
                .build());
        event.reply("Le salon de notification a été mis à jour").queue();
    }

    private void setDmNotifications(SlashCommandInteractionEvent event) {
        boolean enabled = event.getOption("enabled").getAsBoolean();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder().dmNotifications(enabled).build());
        event.reply("Les notifications par message privé ont été mises à jour").queue();
    }

    private void setLevelNotifications(SlashCommandInteractionEvent event) {
        boolean enabled = event.getOption("enabled").getAsBoolean();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder().levelNotificationEnabled(enabled).build());
        event.reply("Les notifications de montée de niveau ont été mises à jour").queue();
    }

    private void setRewardNotifications(SlashCommandInteractionEvent event) {
        boolean enabled = event.getOption("enabled").getAsBoolean();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder().rewardNotificationEnabled(enabled).build());
        event.reply("Les notifications de récompense ont été mises à jour").queue();
    }

    private void setNotificationMessage(SlashCommandInteractionEvent event) {
        event.replyModal(net.dv8tion.jda.api.interactions.modals.Modal.create("set-notification-message", "Changer le message de notification")
                        .addActionRow(TextInput.create("message", "Message de notification", TextInputStyle.PARAGRAPH)
                                .build())
                        .build())
                .queue();
    }

    private void setRewardNotificationMessage(SlashCommandInteractionEvent event) {
        event.replyModal(net.dv8tion.jda.api.interactions.modals.Modal.create("set-reward-notification-message", "Changer le message de notification de récompense")
                        .addActionRow(TextInput.create("message", "Message de notification", TextInputStyle.PARAGRAPH)
                                .build())
                        .build())
                .queue();
    }

    private void sendPlaceholders(SlashCommandInteractionEvent event) {
        event.reply("""
                Voici la liste des placeholders utilisables dans les messages de notification :
                - `%user.mention%` : Mentionne l'utilisateur
                - `%user.name%` : Nom de l'utilisateur
                - `%user.id%` : Id de l'utilisateur
                
                - `%reward.mention%` : Mentionne le rôle gagné
                - `%reward.name%` : Nom du rôle gagné
                - `%reward.id%` : Id du rôle gagné
                
                - `%level%` : Niveau atteint
                """).queue();
    }

    @ModalHandler("set-reward-notification-message")
    public void setRewardNotificationMessage(ModalInteractionEvent event) {
        String message = event.getValue("message").getAsString();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder().rewardNotificationMessage(message).build());
        event.reply("Le message de notification de récompense a été mis à jour").queue();
    }

    @ModalHandler("set-notification-message")
    public void setNotificationMessage(ModalInteractionEvent event) {
        String message = event.getValue("message").getAsString();

        settingsService.applyAndSave(event.getGuild(), settings -> settings.toBuilder().notificationMessage(message).build());
        event.reply("Le message de notification a été mis à jour").queue();
    }
}

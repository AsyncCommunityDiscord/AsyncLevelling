package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GravenGuild;
import fr.redstom.asynclevelling.jpa.entities.GravenGuildSettings;
import fr.redstom.asynclevelling.jpa.repositories.GuildSettingsRepository;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Guild;

import org.springframework.stereotype.Service;

import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class GuildSettingsService {

    private final GuildService guildService;
    private final GuildSettingsRepository settingsRepository;

    public GravenGuildSettings getOrCreateByGuild(Guild guild) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return settingsRepository
                .findByGuild(gGuild)
                .orElseGet(
                        () ->
                                settingsRepository.save(
                                        GravenGuildSettings.builder().guild(gGuild).build()));
    }

    public GravenGuildSettings applyAndSave(
            Guild guild, UnaryOperator<GravenGuildSettings> settings) {
        GravenGuildSettings gSettings = getOrCreateByGuild(guild);
        gSettings = settings.apply(gSettings);

        return settingsRepository.save(gSettings);
    }
}

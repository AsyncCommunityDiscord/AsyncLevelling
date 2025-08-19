package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.GuildSettingsDao;
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

    public GuildSettingsDao getOrCreateByGuild(Guild guild) {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return settingsRepository
                .findByGuild(gGuild)
                .orElseGet(
                        () ->
                                settingsRepository.save(
                                        GuildSettingsDao.builder().guild(gGuild).build()));
    }

    public GuildSettingsDao applyAndSave(
            Guild guild, UnaryOperator<GuildSettingsDao> settings) {
        GuildSettingsDao gSettings = getOrCreateByGuild(guild);
        gSettings = settings.apply(gSettings);

        return settingsRepository.save(gSettings);
    }
}

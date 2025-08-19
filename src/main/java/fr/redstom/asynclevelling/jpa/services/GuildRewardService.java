package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.GuildRewardDao;
import fr.redstom.asynclevelling.jpa.repositories.GuildRewardRepository;

import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GuildRewardService {

    private final GuildRewardRepository guildRewardRepository;

    private final GuildService guildService;

    @Transactional
    public List<GuildRewardDao> getRewardsForGuild(Guild guild) {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return guildRewardRepository.findAllByGuildOrderByLevelAsc(gGuild);
    }

    @Transactional
    public GuildRewardDao createReward(Guild guild, long level, Role roleToGive)
            throws DataIntegrityViolationException {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(guild);

        GuildRewardDao reward =
                GuildRewardDao.builder()
                        .guild(gGuild)
                        .level(level)
                        .roleId(roleToGive.getIdLong())
                        .build();

        return guildRewardRepository.save(reward);
    }

    public Optional<GuildRewardDao> getRewardForGuildAtLevel(Guild guild, long level) {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findByGuildAndLevel(gGuild, level);
    }

    public Optional<GuildRewardDao> getClosestRewardForGuildAtLevel(Guild guild, long level) {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findTopByGuildAndLevelOrderByLevelDesc(gGuild, level);
    }

    public void grantReward(Member member, long level) {
        Optional<GuildRewardDao> oReward =
                getClosestRewardForGuildAtLevel(member.getGuild(), level);
        if (oReward.isEmpty()) {
            return;
        }

        GuildRewardDao reward = oReward.get();
        if (member.getRoles().stream().anyMatch(role -> role.getIdLong() == reward.roleId())) {
            return;
        }

        Role role = member.getGuild().getRoleById(reward.roleId());
        if (role == null) {
            return;
        }

        member.getGuild().addRoleToMember(member, role).queue();
        log.info(
                "{} has gained reward {} in guild {}",
                member.getUser().getAsTag(),
                role.getName(),
                role.getGuild().getName());
    }

    public Optional<GuildRewardDao> getByMemberRole(Member member, Role role) {
        GuildDao gGuild = guildService.getOrCreateByDiscordGuild(member.getGuild());
        return guildRewardRepository.findByGuildAndRoleId(gGuild, role.getIdLong());
    }

    public void delete(GuildRewardDao reward) {
        guildRewardRepository.delete(reward);
    }
}

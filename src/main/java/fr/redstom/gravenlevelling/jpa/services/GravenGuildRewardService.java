package fr.redstom.gravenlevelling.jpa.services;

import fr.redstom.gravenlevelling.jpa.entities.GravenGuild;
import fr.redstom.gravenlevelling.jpa.entities.GravenGuildReward;
import fr.redstom.gravenlevelling.jpa.repositories.GravenGuildRewardRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GravenGuildRewardService {

    private final GravenGuildRewardRepository guildRewardRepository;

    private final GravenGuildService guildService;

    @Transactional
    public List<GravenGuildReward> getRewardsForGuild(Guild guild) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return guildRewardRepository.findAllByGuildOrderByLevelAsc(gGuild);
    }

    @Transactional
    public GravenGuildReward createReward(Guild guild, long level, Role roleToGive) throws DataIntegrityViolationException {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        GravenGuildReward reward = GravenGuildReward.builder()
                .guild(gGuild)
                .level(level)
                .roleId(roleToGive.getIdLong())
                .build();

        return guildRewardRepository.save(reward);
    }

    public Optional<GravenGuildReward> getRewardForGuildAtLevel(Guild guild, long level) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findByGuildAndLevel(gGuild, level);
    }

    public Optional<GravenGuildReward> getClosestRewardForGuildAtLevel(Guild guild, long level) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);
        return guildRewardRepository.findTopByGuildAndLevelOrderByLevelDesc(gGuild, level);
    }

    public void grantReward(Member member, long level) {
        Optional<GravenGuildReward> oReward = getClosestRewardForGuildAtLevel(member.getGuild(), level);
        if (oReward.isEmpty()) {
            return;
        }

        GravenGuildReward reward = oReward.get();
        if (member.getRoles().stream().anyMatch(role -> role.getIdLong() == reward.roleId())) {
            return;
        }

        Role role = member.getGuild().getRoleById(reward.roleId());
        if (role == null) {
            return;
        }

        member.getGuild().addRoleToMember(member, role).queue();
        log.info("{} has gained reward {} in guild {}", member.getUser().getAsTag(), role.getName(), role.getGuild().getName());
    }

    public Optional<GravenGuildReward> getByMemberRole(Member member, Role role) {
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(member.getGuild());
        return guildRewardRepository.findByGuildAndRoleId(gGuild, role.getIdLong());
    }

    public void delete(GravenGuildReward reward) {
        guildRewardRepository.delete(reward);
    }
}

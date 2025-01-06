package fr.redstom.gravenlevelling.jpa.services;

import fr.redstom.gravenlevelling.jpa.entities.GravenGuild;
import fr.redstom.gravenlevelling.jpa.entities.GravenGuildSettings;
import fr.redstom.gravenlevelling.jpa.entities.GravenMember;
import fr.redstom.gravenlevelling.jpa.entities.GravenUser;
import fr.redstom.gravenlevelling.jpa.repositories.GravenMemberRepository;
import fr.redstom.gravenlevelling.utils.LevelUtils;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Lazy)
public class GravenMemberService {

    private final LevelUtils levelUtils;

    private final GravenMemberRepository memberRepository;

    private final GravenGuildService guildService;
    private final GravenUserService userService;
    private final GravenGuildRewardService rewardService;
    private final GuildNotificationService notificationService;
    private final GravenGuildSettingsService settingsService;

    private final JDA jda;

    @Value("${xp.timeout}")
    private int timeout = 60;

    @Transactional
    public GravenMember getMemberByDiscordMember(Member member) {
        GravenUser user = userService.getOrCreateByDiscordUser(member.getUser());
        GravenGuild guild = guildService.getOrCreateByDiscordGuild(member.getGuild());

        return memberRepository
                .findByUserAndGuild(user, guild)
                .orElseGet(() -> this.createMember(member, user, guild));
    }

    @Transactional
    public GravenMember getMemberByGuildAndMemberId(Guild guild, long userId, long baseLevel) {
        GravenUser user = userService.getOrCreateByUserId(userId);
        GravenGuild gGuild = guildService.getOrCreateByDiscordGuild(guild);

        return memberRepository
                .findByUserAndGuild(user, gGuild)
                .orElseGet(
                        () ->
                                memberRepository.save(
                                        GravenMember.builder()
                                                .user(user)
                                                .guild(gGuild)
                                                .level(baseLevel)
                                                .build()));
    }

    private GravenMember createMember(Member member, GravenUser user, GravenGuild guild) {
        GravenGuildSettings settings = settingsService.getOrCreateByGuild(member.getGuild());

        AtomicLong level = new AtomicLong();
        if (settings.autoLevelGrant()) {
            for (Role role : member.getRoles()) {
                rewardService
                        .getByMemberRole(member, role)
                        .ifPresent(
                                reward -> {
                                    if (reward.level() > level.get()) {
                                        level.set(reward.level());
                                    }
                                });
            }
        }

        return memberRepository.save(
                GravenMember.builder().user(user).guild(guild).level(level.get()).build());
    }

    @Transactional
    public boolean addXpFromMessage(Member member, Message message) {
        if (settingsService.getOrCreateByGuild(member.getGuild()).pause()) {
            return false;
        }

        GravenMember gMember = getMemberByDiscordMember(member);

        Instant messageCreated = message.getTimeCreated().toInstant();

        long distance = ChronoUnit.SECONDS.between(messageCreated, gMember.lastMessageAt());
        if (Math.abs(distance) < timeout) return false;

        long xpToGain = levelUtils.flattenMessageLengthIntoGain(message.getContentRaw().length());

        gMember.experience(gMember.experience() + xpToGain);
        gMember.lastMessageAt(messageCreated);

        checkLevel(member);
        memberRepository.save(gMember);

        log.info("{} got added {} xp from message.", member.getUser().getAsTag(), xpToGain);
        return true;
    }

    @Transactional
    public void addXp(Member member, long amount, String reason) {
        if (settingsService.getOrCreateByGuild(member.getGuild()).pause()) {
            return;
        }

        GravenMember gMember = getMemberByDiscordMember(member);

        gMember.experience(gMember.experience() + amount);
        memberRepository.save(gMember);
        log.info(
                "{} got added {} xp for {} in guild {}.",
                member.getUser().getAsTag(),
                amount,
                reason,
                member.getGuild().getName());

        checkLevel(member);
    }

    @Transactional
    public void addXp(Member member, long amount) {
        addXp(member, amount, "Unknown reason");
    }

    @Transactional
    public boolean checkLevel(Member member) {
        GravenMember gMember = getMemberByDiscordMember(member);

        long xp = gMember.experience();
        long xpToNextLevel = levelUtils.xpForNextLevelAt(gMember.level());

        if (xp < xpToNextLevel) {
            return false;
        }

        gMember.experience(gMember.experience() % xpToNextLevel);
        gMember.level(gMember.level() + 1);
        rewardService.grantReward(member, gMember.level());
        notificationService.sendNotification(member, gMember.level());

        memberRepository.save(gMember);
        log.info(
                "{} has levelled up to level {} in guild {}.",
                member.getUser().getAsTag(),
                gMember.level(),
                gMember.level());

        return true;
    }

    @Transactional
    public int getRank(Member member) {
        GravenMember gMember = getMemberByDiscordMember(member);
        return memberRepository.findPositionOfMember(gMember.user(), gMember.guild());
    }

    @Nullable
    public Member getDiscordMemberByMember(GravenMember gravenMember) {
        Guild guild = jda.getGuildById(gravenMember.guild().id());
        Member member = guild.getMemberById(gravenMember.user().id());

        if (member == null) {
            try {
                member =
                        guild.retrieveMemberById(gravenMember.user().id())
                                .useCache(true)
                                .complete();
            } catch (Exception e) {
                return null;
            }
        }

        return member;
    }
}

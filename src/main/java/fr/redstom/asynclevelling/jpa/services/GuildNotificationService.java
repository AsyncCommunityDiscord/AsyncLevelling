package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GravenGuildReward;
import fr.redstom.asynclevelling.jpa.entities.GravenGuildSettings;
import fr.redstom.asynclevelling.jpa.entities.GravenMember;
import fr.redstom.asynclevelling.utils.PlaceholderMessage;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class GuildNotificationService {

    private final GravenGuildSettingsService settingsService;
    private final GravenGuildRewardService rewardService;
    private final GravenMemberService memberService;

    public Optional<Message> sendNotification(Member member, long level) {
        GravenGuildSettings settings = settingsService.getOrCreateByGuild(member.getGuild());
        Optional<GravenGuildReward> reward =
                rewardService.getRewardForGuildAtLevel(member.getGuild(), level);

        if (reward.isPresent() && !settings.rewardNotificationEnabled()) {
            reward = Optional.empty();
        }

        if (reward.isEmpty() && !settings.levelNotificationEnabled()) {
            return Optional.empty();
        }

        if (settings.dmNotifications()) {
            return sendDmNotification(member, settings, reward);
        }

        if (settings.notificationChannelId() >= 0) {
            return sendServerNotification(member, settings, reward);
        }

        return Optional.empty();
    }

    private Optional<Message> sendDmNotification(
            Member member, GravenGuildSettings settings, Optional<GravenGuildReward> reward) {
        GravenMember gMember = memberService.getMemberByDiscordMember(member);

        PrivateChannel channel = member.getUser().openPrivateChannel().complete();

        String message =
                reward.map(r -> getRewardMessage(member, gMember, settings, r))
                        .orElseGet(() -> getMessage(member, gMember, settings));

        return Optional.of(
                channel.sendMessage(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .complete());
    }

    private Optional<Message> sendServerNotification(
            Member member, GravenGuildSettings settings, Optional<GravenGuildReward> reward) {
        GravenMember gMember = memberService.getMemberByDiscordMember(member);

        MessageChannel channel =
                (MessageChannel)
                        member.getGuild()
                                .getGuildChannelById(
                                        settings.notificationChannelType(),
                                        settings.notificationChannelId());
        if (channel == null) {
            return Optional.empty();
        }

        String message =
                reward.map(r -> getRewardMessage(member, gMember, settings, r))
                        .orElseGet(() -> getMessage(member, gMember, settings));

        return Optional.of(
                channel.sendMessage(message)
                        .setAllowedMentions(List.of(Message.MentionType.USER))
                        .complete());
    }

    private String getMessage(Member member, GravenMember gMember, GravenGuildSettings settings) {
        return new PlaceholderMessage(settings.notificationMessage())
                .with("user.mention", member.getAsMention())
                .with("user.name", member.getEffectiveName())
                .with("user.id", member.getId())
                .with("level", String.valueOf(gMember.level()))
                .replace();
    }

    private String getRewardMessage(
            Member member,
            GravenMember gMember,
            GravenGuildSettings settings,
            GravenGuildReward reward) {
        Role role = member.getGuild().getRoleById(reward.roleId());

        return new PlaceholderMessage(settings.rewardNotificationMessage())
                .with("user.mention", member.getAsMention())
                .with("user.name", member.getEffectiveName())
                .with("user.id", member.getId())
                .with("reward.mention", role == null ? "<@&1>" : role.getAsMention())
                .with("reward.name", role == null ? "undefined" : role.getName())
                .with("reward.id", role == null ? "undefined" : role.getId())
                .with("level", String.valueOf(gMember.level()))
                .replace();
    }
}

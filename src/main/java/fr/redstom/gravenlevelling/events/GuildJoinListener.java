package fr.redstom.gravenlevelling.events;

import fr.redstom.gravenlevelling.jpa.entities.GravenMember;
import fr.redstom.gravenlevelling.jpa.services.GravenGuildRewardService;
import fr.redstom.gravenlevelling.jpa.services.GravenMemberService;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuildJoinListener extends ListenerAdapter {

    private final GravenMemberService memberService;
    private final GravenGuildRewardService rewardService;

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member member = event.getMember();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        rewardService.grantReward(member, gMember.level());
    }
}

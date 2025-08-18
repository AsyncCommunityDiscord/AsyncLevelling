package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GravenGuild;
import fr.redstom.asynclevelling.jpa.entities.GravenMember;
import fr.redstom.asynclevelling.jpa.repositories.GravenGuildRepository;
import fr.redstom.asynclevelling.jpa.repositories.GravenMemberRepository;
import fr.redstom.asynclevelling.utils.ImageGenerator;

import jakarta.annotation.Nullable;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Lazy))
public class GuildService {

    private final GravenGuildRepository guildRepository;
    private final GravenMemberRepository memberRepository;

    private final MemberService memberService;

    private final ImageGenerator imageGenerator;

    public GravenGuild getOrCreateByDiscordGuild(Guild guild) {
        return guildRepository
                .findById(guild.getIdLong())
                .orElseGet(
                        () ->
                                guildRepository.save(
                                        GravenGuild.builder().id(guild.getIdLong()).build()));
    }

    @Transactional
    public Page<GravenMember> getLeaderboardOf(Guild guild, int page) {
        GravenGuild gGuild = getOrCreateByDiscordGuild(guild);

        return memberRepository.findAllByGuild(gGuild, PageRequest.of(page - 1, 10));
    }

    @Transactional
    @SneakyThrows
    public byte[] getLeaderboardImageFor(Guild guild, int page, @Nullable Member member) {
        Page<GravenMember> members = getLeaderboardOf(guild, page);
        if (members.isEmpty()) {
            return null;
        }

        GravenMember gMember =
                member == null ? null : memberService.getMemberByDiscordMember(member);
        BufferedImage image =
                imageGenerator.generateLeaderboardImage(
                        page,
                        gMember,
                        members.getContent(),
                        memberService::getDiscordMemberByMember);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", stream);
        stream.flush();

        return stream.toByteArray();
    }
}

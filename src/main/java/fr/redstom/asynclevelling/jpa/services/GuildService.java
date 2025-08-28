package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.MemberDao;
import fr.redstom.asynclevelling.jpa.repositories.GuildRepository;
import fr.redstom.asynclevelling.jpa.repositories.MemberRepository;
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

    private final GuildRepository guildRepository;
    private final MemberRepository memberRepository;

    private final MemberService memberService;

    private final ImageGenerator imageGenerator;

    public GuildDao getOrCreateByDiscordGuild(Guild guild) {
        return guildRepository
                .findById(guild.getIdLong())
                .orElseGet(
                        () ->
                                guildRepository.save(
                                        GuildDao.builder().id(guild.getIdLong()).build()));
    }

    @Transactional
    public Page<MemberDao> getLeaderboardOf(Guild guild, int page) {
        GuildDao gGuild = getOrCreateByDiscordGuild(guild);

        return memberRepository.findAllByGuild(gGuild, PageRequest.of(page - 1, 10));
    }

    @Transactional
    @SneakyThrows
    public byte[] getLeaderboardImageFor(Guild guild, int page, @Nullable Member member) {
        Page<MemberDao> members = getLeaderboardOf(guild, page);
        if (members.isEmpty()) {
            return null;
        }

        MemberDao gMember = member == null ? null : memberService.getMemberByDiscordMember(member);
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

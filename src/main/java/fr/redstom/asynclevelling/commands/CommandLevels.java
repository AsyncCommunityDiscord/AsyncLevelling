package fr.redstom.asynclevelling.commands;

import fr.redstom.asynclevelling.jpa.entities.GravenMember;
import fr.redstom.asynclevelling.jpa.repositories.GravenMemberRepository;
import fr.redstom.asynclevelling.jpa.services.MemberService;
import fr.redstom.asynclevelling.utils.jda.Command;
import fr.redstom.asynclevelling.utils.jda.CommandExecutor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

@Slf4j
@Command
@RequiredArgsConstructor
public class CommandLevels implements CommandExecutor {

    private final MemberService memberService;
    private final GravenMemberRepository memberRepository;

    @Override
    public SlashCommandData data() {
        return Commands.slash("levels", "Permet de gérer les niveaux des membres")
                .addSubcommands(
                        new SubcommandData("add", "Ajoute des niveaux à un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur", true)
                                .addOption(OptionType.INTEGER, "lvl", "Niveaux à ajouter", true),
                        new SubcommandData("remove", "Retire des niveaux à un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur", true)
                                .addOption(OptionType.INTEGER, "lvl", "Niveaux à retirer", true),
                        new SubcommandData("set", "Définit le niveau d'un membre")
                                .addOption(OptionType.USER, "user", "Utilisateur", true)
                                .addOption(OptionType.INTEGER, "lvl", "Niveau à définir", true))
                .setDefaultPermissions(DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        switch (event.getSubcommandName()) {
            case "add" -> this.add(event);
            case "remove" -> this.remove(event);
            case "set" -> this.set(event);
        }
    }

    private void add(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        long oldLevel = gMember.level();

        gMember.level(oldLevel + lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(
                        "✅ L'utilisateur "
                                + member.getAsMention()
                                + " est maintenant au niveau **"
                                + gMember.level()
                                + "**")
                .queue();
        log.info(
                "{} changed level of {} from {} to {} in guild {}",
                event.getMember().getUser().getAsTag(),
                member.getUser().getAsTag(),
                oldLevel,
                oldLevel + lvl,
                event.getGuild().getName());
    }

    private void remove(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        long oldLevel = gMember.level();

        gMember.level(oldLevel - lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(
                        "✅ L'utilisateur "
                                + member.getAsMention()
                                + " est maintenant au niveau **"
                                + gMember.level()
                                + "**")
                .queue();
        log.info(
                "{} changed level of {} from {} to {} in guild {}",
                event.getMember().getUser().getAsTag(),
                member.getUser().getAsTag(),
                oldLevel,
                oldLevel - lvl,
                event.getGuild().getName());
    }

    private void set(SlashCommandInteractionEvent event) {
        Member member = event.getOption("user").getAsMember();
        int lvl = event.getOption("lvl").getAsInt();

        GravenMember gMember = memberService.getMemberByDiscordMember(member);
        long oldLevel = gMember.level();

        gMember.level(lvl);
        gMember.experience(0);

        memberRepository.save(gMember);

        event.reply(
                        "✅ L'utilisateur "
                                + member.getAsMention()
                                + " est maintenant au niveau **"
                                + gMember.level()
                                + "**")
                .queue();
        log.info(
                "{} changed level of {} from {} to {} in guild {}",
                event.getMember().getUser().getAsTag(),
                member.getUser().getAsTag(),
                oldLevel,
                lvl,
                event.getGuild().getName());
    }
}

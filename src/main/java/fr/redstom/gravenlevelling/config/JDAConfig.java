package fr.redstom.gravenlevelling.config;

import fr.redstom.gravenlevelling.utils.jda.CommandExecutor;

import lombok.extern.slf4j.Slf4j;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@Slf4j
public class JDAConfig {

    @Bean
    JDA client(
            @Value("${bot.token}") String token,
            List<ListenerAdapter> eventListeners,
            List<CommandExecutor> commandExecutors)
            throws InterruptedException {
        JDA client =
                JDABuilder.create(
                                token,
                                GatewayIntent.GUILD_MESSAGES,
                                GatewayIntent.MESSAGE_CONTENT,
                                GatewayIntent.GUILD_MEMBERS,
                                GatewayIntent.GUILD_VOICE_STATES)
                        .setMemberCachePolicy(MemberCachePolicy.ALL)
                        .build();

        log.info(
                "Event listeners found: {}",
                String.join(
                        ", ",
                        eventListeners.stream()
                                .map(Object::getClass)
                                .map(Class::getName)
                                .toList()));
        log.info(
                "Commands found: {}",
                String.join(
                        ", ",
                        commandExecutors.stream()
                                .map(Object::getClass)
                                .map(Class::getName)
                                .toList()));

        client.addEventListener(eventListeners.toArray());

        client.updateCommands()
                .addCommands(commandExecutors.stream().map(CommandExecutor::data).toList())
                .queue();

        return client.awaitReady();
    }
}

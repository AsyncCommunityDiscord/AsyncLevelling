package fr.redstom.asynclevelling.events;

import fr.redstom.asynclevelling.jpa.services.MemberService;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageListener extends ListenerAdapter {

    private final MemberService memberService;

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (!event.isFromGuild()) return;
        if (event.getAuthor().isBot()
                || event.getAuthor().isSystem()
                || event.isWebhookMessage()
                || event.getMember() == null) return;

        memberService.addXpFromMessage(event.getMember(), event.getMessage());
    }
}

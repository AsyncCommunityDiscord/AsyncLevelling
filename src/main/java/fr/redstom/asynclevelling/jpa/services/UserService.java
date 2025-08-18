package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.GravenUser;
import fr.redstom.asynclevelling.jpa.repositories.GravenUserRepository;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.User;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GravenUserRepository userRepository;

    public GravenUser getOrCreateByDiscordUser(User user) {
        return userRepository
                .findById(user.getIdLong())
                .orElseGet(
                        () ->
                                userRepository.save(
                                        GravenUser.builder().id(user.getIdLong()).build()));
    }

    public GravenUser getOrCreateByUserId(long userId) {
        return userRepository
                .findById(userId)
                .orElseGet(() -> userRepository.save(GravenUser.builder().id(userId).build()));
    }
}

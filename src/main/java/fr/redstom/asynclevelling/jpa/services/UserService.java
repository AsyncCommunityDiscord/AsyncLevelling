package fr.redstom.asynclevelling.jpa.services;

import fr.redstom.asynclevelling.jpa.entities.UserDao;
import fr.redstom.asynclevelling.jpa.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

import net.dv8tion.jda.api.entities.User;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDao getOrCreateByDiscordUser(User user) {
        return userRepository
                .findById(user.getIdLong())
                .orElseGet(
                        () ->
                                userRepository.save(
                                        UserDao.builder().id(user.getIdLong()).build()));
    }

    public UserDao getOrCreateByUserId(long userId) {
        return userRepository
                .findById(userId)
                .orElseGet(() -> userRepository.save(UserDao.builder().id(userId).build()));
    }
}

package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.GuildRewardDao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuildRewardRepository extends CrudRepository<GuildRewardDao, Long> {

    List<GuildRewardDao> findAllByGuildOrderByLevelAsc(GuildDao guild);

    Optional<GuildRewardDao> findTopByGuildAndLevelOrderByLevelDesc(
            GuildDao guild, long level);

    Optional<GuildRewardDao> findByGuildAndLevel(GuildDao guild, long level);

    Optional<GuildRewardDao> findByGuildAndRoleId(GuildDao guild, long roleId);
}

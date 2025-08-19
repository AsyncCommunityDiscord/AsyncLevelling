package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.GuildSettingsDao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuildSettingsRepository extends CrudRepository<GuildSettingsDao, Long> {

    Optional<GuildSettingsDao> findByGuild(GuildDao gGuild);
}

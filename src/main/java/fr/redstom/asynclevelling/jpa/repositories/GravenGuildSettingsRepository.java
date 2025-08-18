package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.GravenGuild;
import fr.redstom.asynclevelling.jpa.entities.GravenGuildSettings;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GravenGuildSettingsRepository extends CrudRepository<GravenGuildSettings, Long> {

    Optional<GravenGuildSettings> findByGuild(GravenGuild gGuild);
}

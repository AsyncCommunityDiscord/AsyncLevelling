package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Table(name = "graven_guild")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GuildDao {

    @Id private Long id;
}

package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(name = "graven_guild_reward", uniqueConstraints = {@UniqueConstraint(columnNames = {"guild_id", "level"})})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GuildRewardDao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GuildDao guild;

    private long level;

    private long roleId;
}

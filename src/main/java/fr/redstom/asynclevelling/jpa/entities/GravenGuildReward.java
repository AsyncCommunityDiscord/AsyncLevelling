package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.*;

import lombok.*;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"guild_id", "level"})})
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GravenGuildReward {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @ManyToOne
    @JoinColumn(name = "guild_id")
    private GravenGuild guild;

    private long level;

    private long roleId;
}

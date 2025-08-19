package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "graven_member")
@IdClass(MemberDao.MemberDaoId.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberDao {

    @ManyToOne @Id private UserDao user;

    @ManyToOne @Id private GuildDao guild;

    @Builder.Default private Instant lastMessageAt = Instant.EPOCH;

    private long level;
    private long experience;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class MemberDaoId implements Serializable {
        private UserDao user;

        private GuildDao guild;
    }
}

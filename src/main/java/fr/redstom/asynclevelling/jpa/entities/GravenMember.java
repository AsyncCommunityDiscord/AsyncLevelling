package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.*;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table
@IdClass(GravenMember.GravenMemberId.class)
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GravenMember {

    @ManyToOne @Id private GravenUser user;

    @ManyToOne @Id private GravenGuild guild;

    @Builder.Default private Instant lastMessageAt = Instant.EPOCH;

    private long level;
    private long experience;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class GravenMemberId implements Serializable {
        private GravenUser user;

        private GravenGuild guild;
    }
}

package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.*;

import lombok.*;

import net.dv8tion.jda.api.entities.channel.ChannelType;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class GravenGuildSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long dummyId;

    @OneToOne private GravenGuild guild;

    @Builder.Default private long notificationChannelId = -1;
    @Builder.Default private ChannelType notificationChannelType = ChannelType.UNKNOWN;

    @Builder.Default private boolean dmNotifications = false;

    @Builder.Default private boolean levelNotificationEnabled = true;
    @Builder.Default private boolean rewardNotificationEnabled = true;

    @Builder.Default private boolean autoLevelGrant = true;

    @Builder.Default private boolean pause = true;

    @Builder.Default
    private String notificationMessage = "Bravo %user.mention%, tu as atteint le niveau %level% !";

    @Builder.Default
    private String rewardNotificationMessage =
            """
            Bravo %user.mention%, tu as atteint le niveau %level% !
            Tu as gagné le rôle %reward.mention% !
            """;
}

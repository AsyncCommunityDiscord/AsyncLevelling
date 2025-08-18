package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.GravenGuild;
import fr.redstom.asynclevelling.jpa.entities.GravenMember;
import fr.redstom.asynclevelling.jpa.entities.GravenUser;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GravenMemberRepository
        extends CrudRepository<GravenMember, GravenMember.GravenMemberId> {

    List<GravenMember> findAllByUser(GravenUser user);

    List<GravenMember> findAllByUserId(long userId);

    List<GravenMember> findAllByGuild(GravenGuild guild);

    List<GravenMember> findAllByGuildId(long guildId);

    @Query("select g from GravenMember g where g.user = ?1 and g.guild = ?2")
    Optional<GravenMember> findByUserAndGuild(GravenUser user, GravenGuild guild);

    @Query(
            """
    SELECT memberRank.rank
    FROM (
        SELECT gm.user as user,
               ROW_NUMBER() OVER (ORDER BY gm.level DESC, gm.experience DESC, gm.user.id ASC) AS rank
        FROM GravenMember gm
        WHERE gm.guild = :guild
        AND NOT (gm.level = 0 AND gm.experience = 0)
    ) memberRank
    WHERE memberRank.user = :user
""")
    int findPositionOfMember(@Param("user") GravenUser user, @Param("guild") GravenGuild guild);

    @Query(
            """
            SELECT g
            FROM GravenMember g
            WHERE
                g.guild = ?1
                AND NOT (g.level = 0 AND g.experience = 0)
            ORDER BY
                g.level DESC,
                g.experience DESC,
                g.user.id ASC
            """)
    Page<GravenMember> findAllByGuild(GravenGuild guild, Pageable config);
}

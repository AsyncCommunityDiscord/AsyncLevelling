package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.GuildDao;
import fr.redstom.asynclevelling.jpa.entities.MemberDao;
import fr.redstom.asynclevelling.jpa.entities.UserDao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MemberRepository extends CrudRepository<MemberDao, MemberDao.MemberDaoId> {

    List<MemberDao> findAllByUser(UserDao user);

    List<MemberDao> findAllByUserId(long userId);

    List<MemberDao> findAllByGuild(GuildDao guild);

    List<MemberDao> findAllByGuildId(long guildId);

    @Query("select g from MemberDao g where g.user = ?1 and g.guild = ?2")
    Optional<MemberDao> findByUserAndGuild(UserDao user, GuildDao guild);

    @Query(
"""
    SELECT memberRank.rank
    FROM (
        SELECT gm.user as user,
               ROW_NUMBER() OVER (ORDER BY gm.level DESC, gm.experience DESC, gm.user.id ASC) AS rank
        FROM MemberDao gm
        WHERE gm.guild = :guild
        AND NOT (gm.level = 0 AND gm.experience = 0)
    ) memberRank
    WHERE memberRank.user = :user
""")
    int findPositionOfMember(@Param("user") UserDao user, @Param("guild") GuildDao guild);

    @Query(
            """
            SELECT g
            FROM MemberDao g
            WHERE
                g.guild = ?1
                AND NOT (g.level = 0 AND g.experience = 0)
            ORDER BY
                g.level DESC,
                g.experience DESC,
                g.user.id ASC
            """)
    Page<MemberDao> findAllByGuild(GuildDao guild, Pageable config);
}

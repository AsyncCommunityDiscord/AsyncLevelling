package fr.redstom.asynclevelling.jpa.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.*;

@Entity
@Table(name = "graven_user")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDao {

    @Id private long id;
}

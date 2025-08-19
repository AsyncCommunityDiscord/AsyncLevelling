package fr.redstom.asynclevelling.jpa.repositories;

import fr.redstom.asynclevelling.jpa.entities.UserDao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserDao, Long> {}

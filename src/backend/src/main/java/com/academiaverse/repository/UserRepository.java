package com.academiaverse.repository;

import com.academiaverse.entity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String username);

}
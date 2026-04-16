package com.academiaverse.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.academiaverse.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

    Optional<User> findByEmail(String username);

}
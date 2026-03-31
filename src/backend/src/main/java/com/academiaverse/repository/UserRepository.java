package com.academiaverse.repository;

import org.springframework.data.repository.CrudRepository;

import com.academiaverse.entity.User;

public interface UserRepository extends CrudRepository<User, Integer> {

}
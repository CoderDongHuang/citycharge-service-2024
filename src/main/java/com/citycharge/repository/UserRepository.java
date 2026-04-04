package com.citycharge.repository;

import com.citycharge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByGithubId(String githubId);
    
    @Query("SELECT u FROM User u WHERE u.username = :login OR u.email = :login")
    Optional<User> findByUsernameOrEmail(@Param("login") String login);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}

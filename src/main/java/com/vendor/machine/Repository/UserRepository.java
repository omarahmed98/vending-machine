package com.vendor.machine.Repository;

import com.vendor.machine.Entity.User;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);


    @Transactional
    @Modifying
    @Query(value = "UPDATE User u SET u.username = :#{#data.username} ," +
                    " u.deposit = :#{#data.deposit} ," +
                    " u.role = :#{#data.role} " +
                    "WHERE u.id = :#{#data.id}")
    int updateUserData( @Param("data") User data );
}

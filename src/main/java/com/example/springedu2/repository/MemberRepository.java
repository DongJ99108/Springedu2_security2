package com.example.springedu2.repository;

import com.example.springedu2.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUsername(String username);
    boolean existsByEmailIgnoreCase(String email);

    Optional<Member> findByUsername(String username);

}

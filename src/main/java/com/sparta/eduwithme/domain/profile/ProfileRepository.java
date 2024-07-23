package com.sparta.eduwithme.domain.profile;

import com.sparta.eduwithme.domain.profile.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<User, Long> {
}
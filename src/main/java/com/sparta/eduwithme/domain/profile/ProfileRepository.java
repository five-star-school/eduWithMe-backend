package com.sparta.eduwithme.domain.profile;

import com.sparta.eduwithme.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<User, Long> {
}
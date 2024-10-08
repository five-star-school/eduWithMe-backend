package com.sparta.eduwithme.domain.room.repository;

import com.sparta.eduwithme.domain.room.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN FETCH s.room r JOIN FETCH s.user u WHERE r.id = :roomId AND u.id = :userId")
    Optional<Student> findByRoomIdAndUserIdWithJoin(@Param("roomId") Long roomId, @Param("userId") Long userId);

    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.room.id = :roomId")
    List<Student> findByRoomIdWithUser(@Param("roomId") Long roomId);

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.room r WHERE s.user.id = :userId")
    List<Student> findStudentsWithRoomByUserId(@Param("userId") Long userId);

    void deleteAllByUserId(Long userId);

}

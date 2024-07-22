package com.sparta.eduwithme.domain.room;

import com.sparta.eduwithme.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomName(String roomName);

    Long countByManagerUserId(Long managerUserId);
}

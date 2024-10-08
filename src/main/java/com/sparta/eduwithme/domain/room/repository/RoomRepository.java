package com.sparta.eduwithme.domain.room.repository;

import com.sparta.eduwithme.domain.room.entity.Room;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long>, RoomRepositoryCustom {
    Optional<Room> findByRoomName(String roomName);
    Long countByManagerUserId(Long managerUserId);
    Optional<Room> findByIdAndManagerUserId(Long roomId, Long managerUserId);
    Optional<Room> findByIdAndRoomPassword(Long roomId, String roomPassword);
    List<Room> findAllByManagerUserId(Long userId);
}

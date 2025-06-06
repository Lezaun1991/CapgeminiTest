package com.capgemini.test.code.modelo.repository;

import com.capgemini.test.code.modelo.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room,Long> {
}

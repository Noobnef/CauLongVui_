package com.example.CauLongVui.repository;

import com.example.CauLongVui.entity.Court;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourtRepository extends JpaRepository<Court, Long> {
    List<Court> findByStatus(Court.CourtStatus status);
}

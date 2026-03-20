package com.example.CauLongVui.service;

import com.example.CauLongVui.dto.CourtDTO;
import com.example.CauLongVui.entity.Court;
import com.example.CauLongVui.exception.ResourceNotFoundException;
import com.example.CauLongVui.repository.CourtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourtService {

    private final CourtRepository courtRepository;

    @Transactional(readOnly = true)
    public List<CourtDTO> getAllCourts() {
        return courtRepository.findAll().stream()
                .map(CourtDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourtDTO getCourtById(Long id) {
        Court court = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với ID: " + id));
        return CourtDTO.fromEntity(court);
    }

    @Transactional(readOnly = true)
    public List<CourtDTO> getCourtsByStatus(Court.CourtStatus status) {
        return courtRepository.findByStatus(status).stream()
                .map(CourtDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public CourtDTO createCourt(CourtDTO courtDTO) {
        Court court = courtDTO.toEntity();
        Court saved = courtRepository.save(court);
        return CourtDTO.fromEntity(saved);
    }

    public CourtDTO updateCourt(Long id, CourtDTO courtDTO) {
        Court existing = courtRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sân với ID: " + id));
        existing.setName(courtDTO.getName());
        existing.setDescription(courtDTO.getDescription());
        existing.setPricePerHour(courtDTO.getPricePerHour());
        if (courtDTO.getStatus() != null) {
            existing.setStatus(courtDTO.getStatus());
        }
        return CourtDTO.fromEntity(courtRepository.save(existing));
    }

    public void deleteCourt(Long id) {
        if (!courtRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy sân với ID: " + id);
        }
        courtRepository.deleteById(id);
    }
}

package com.example.CauLongVui.dto;

import com.example.CauLongVui.entity.Court;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourtDTO {
    private Long id;
    private String name;
    private String description;
    private Double pricePerHour;
    private Court.CourtStatus status;
    private String imageUrl;

    public static CourtDTO fromEntity(Court court) {
        return CourtDTO.builder()
                .id(court.getId())
                .name(court.getName())
                .description(court.getDescription())
                .pricePerHour(court.getPricePerHour())
                .status(court.getStatus())
                .imageUrl(court.getImageUrl())
                .build();
    }

    public Court toEntity() {
        return Court.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .pricePerHour(this.pricePerHour)
                .status(this.status != null ? this.status : Court.CourtStatus.AVAILABLE)
                .imageUrl(this.imageUrl)
                .build();
    }
}

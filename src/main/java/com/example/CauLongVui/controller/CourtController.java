package com.example.CauLongVui.controller;

import com.example.CauLongVui.dto.ApiResponse;
import com.example.CauLongVui.dto.CourtDTO;
import com.example.CauLongVui.entity.Court;
import com.example.CauLongVui.service.CourtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courts")
@RequiredArgsConstructor
public class CourtController {

    private final CourtService courtService;

    // GET /api/courts — lấy tất cả sân
    @GetMapping
    public ResponseEntity<ApiResponse<List<CourtDTO>>> getAllCourts(
            @RequestParam(name = "status", required = false) Court.CourtStatus status) {
        List<CourtDTO> courts = (status != null)
                ? courtService.getCourtsByStatus(status)
                : courtService.getAllCourts();
        return ResponseEntity.ok(ApiResponse.success(courts));
    }

    // GET /api/courts/{id} — lấy sân theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtDTO>> getCourtById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courtService.getCourtById(id)));
    }

    // POST /api/courts — tạo sân mới
    @PostMapping
    public ResponseEntity<ApiResponse<CourtDTO>> createCourt(@RequestBody CourtDTO courtDTO) {
        CourtDTO created = courtService.createCourt(courtDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo sân thành công", created));
    }

    // PUT /api/courts/{id} — cập nhật sân
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourtDTO>> updateCourt(@PathVariable Long id,
                                                              @RequestBody CourtDTO courtDTO) {
        return ResponseEntity.ok(ApiResponse.success("Cập nhật sân thành công",
                courtService.updateCourt(id, courtDTO)));
    }

    // DELETE /api/courts/{id} — xóa sân
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa sân thành công", null));
    }
}

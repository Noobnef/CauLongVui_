package com.example.CauLongVui.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotNull(message = "ID người dùng không được để trống")
    private Long id;

    @Size(min = 2, max = 100, message = "Họ tên phải từ 2–100 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    /** Để trống nếu không muốn đổi mật khẩu */
    @Size(min = 6, max = 255, message = "Mật khẩu phải từ 6 ký tự")
    private String newPassword;

    /** Xác nhận mật khẩu cũ khi đổi mật khẩu */
    private String currentPassword;
}

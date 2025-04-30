package com.fourclover.clobee.user.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UserInfo {
    private Long userId;


}

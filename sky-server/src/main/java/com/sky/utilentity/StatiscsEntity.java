package com.sky.utilentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatiscsEntity {
    private Integer status;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;
}

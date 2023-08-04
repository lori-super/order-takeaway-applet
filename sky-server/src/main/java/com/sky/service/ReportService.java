package com.sky.service;

import com.sky.result.Result;

import java.time.LocalDate;

public interface ReportService {
    Result turnoverStatistics(LocalDate begin, LocalDate end);

    Result userStatistics(LocalDate begin, LocalDate end);

    Result ordersStatistics(LocalDate begin, LocalDate end);

    Result orderTop10(LocalDate begin, LocalDate end);
}

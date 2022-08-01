package com.glinboy.app.service;

import com.glinboy.app.domain.MessagesStatistics;

import java.util.List;

public interface DashboardService {
    List<MessagesStatistics> getLast30DaysMessagesStatics();
}

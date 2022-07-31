package com.glinboy.app.repository;

import com.glinboy.app.domain.MessagesStatistics;

import java.util.List;

public interface DashboardRepository {
    List<MessagesStatistics> getLast30DaysMessagesStatics();
}

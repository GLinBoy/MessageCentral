package com.glinboy.app.repository;

import com.glinboy.app.domain.MessagesStatics;

import java.util.List;

public interface DashboardRepository {
    List<MessagesStatics> getLast30DaysMessagesStatics();
}

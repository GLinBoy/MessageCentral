package com.glinboy.app.service;

import com.glinboy.app.domain.MessagesStatics;

import java.util.List;

public interface DashboardService {
    List<MessagesStatics> getLast30DaysMessagesStatics();
}

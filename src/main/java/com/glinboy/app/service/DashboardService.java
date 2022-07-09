package com.glinboy.app.service;

import com.glinboy.app.domain.projection.MessagesStatic;

import java.util.List;

public interface DashboardService {
    List<MessagesStatic> getLast30DaysMessageStatics();
}

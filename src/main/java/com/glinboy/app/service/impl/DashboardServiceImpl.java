package com.glinboy.app.service.impl;

import com.glinboy.app.domain.MessagesStatics;
import com.glinboy.app.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {
    @Override
    public List<MessagesStatics> getLast30DaysMessagesStatics() {
        return List.of();
    }
}

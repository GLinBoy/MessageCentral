package com.glinboy.app.service.impl;

import com.glinboy.app.domain.MessagesStatics;
import com.glinboy.app.repository.DashboardRepository;
import com.glinboy.app.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository repository;

    public DashboardServiceImpl(DashboardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MessagesStatics> getLast30DaysMessagesStatics() {
        return repository.getLast30DaysMessagesStatics();
    }
}

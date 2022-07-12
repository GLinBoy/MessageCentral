package com.glinboy.app.repository.impl;

import com.glinboy.app.domain.MessagesStatics;
import com.glinboy.app.repository.DashboardRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    private final JdbcTemplate template;

    public DashboardRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<MessagesStatics> getLast30DaysMessagesStatics() {
        return null;
    }
}

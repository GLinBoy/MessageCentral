package com.glinboy.app.repository.impl;

import com.glinboy.app.domain.MessagesStatistics;
import com.glinboy.app.repository.DashboardRepository;
import com.glinboy.app.repository.rowmapper.MessagesStatisticsRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DashboardRepositoryImpl implements DashboardRepository {

    private final JdbcTemplate template;

    private static final String LAST_30_DAYS_MESSAGES_STATISTICS_QUERY = " SELECT msv.date, " +
        "        msv.total_email_sent, " +
        "        msv.total_email_failed, " +
        "        msv.total_sms_sent, " +
        "        msv.total_sms_failed, " +
        "        msv.total_notification_sent, " +
        "        msv.total_notification_failed " +
        " FROM messages_statistics_vw msv ";

    public DashboardRepositoryImpl(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public List<MessagesStatistics> getLast30DaysMessagesStatics() {
        return template.query(LAST_30_DAYS_MESSAGES_STATISTICS_QUERY, new MessagesStatisticsRowMapper());
    }
}

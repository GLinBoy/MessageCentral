package com.glinboy.app.repository.rowmapper;

import com.glinboy.app.domain.MessagesStatistics;
import com.glinboy.app.domain.Statistics;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class MessagesStatisticsRowMapper implements RowMapper<MessagesStatistics> {
    @Override
    public MessagesStatistics mapRow(ResultSet rs, int rowNum) throws SQLException {
        LocalDateTime dateTime = LocalDate.parse(rs.getString("date")).atStartOfDay();
        return new MessagesStatistics(dateTime.toInstant(ZoneOffset.UTC),
            new Statistics(rs.getLong("total_email_sent"),
                rs.getLong("total_email_failed")),
            new Statistics(rs.getLong("total_sms_sent"),
                rs.getLong("total_sms_failed")),
            new Statistics(rs.getLong("total_notification_sent"),
                rs.getLong("total_notification_failed")));
    }
}

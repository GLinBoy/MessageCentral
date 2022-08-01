package com.glinboy.app.service.impl;

import com.glinboy.app.domain.MessagesStatistics;
import com.glinboy.app.domain.Statistics;
import com.glinboy.app.repository.DashboardRepository;
import com.glinboy.app.service.DashboardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final DashboardRepository repository;

    public DashboardServiceImpl(DashboardRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MessagesStatistics> getLast30DaysMessagesStatics() {
        List<MessagesStatistics> last30DaysMessagesStatics = repository.getLast30DaysMessagesStatics();
        Set<Instant> availableDates = last30DaysMessagesStatics.stream()
            .map(MessagesStatistics::getDate)
            .collect(Collectors.toSet());
        List<MessagesStatistics> messagesStatistics = IntStream.range(1, 31)
            .mapToObj(i -> LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().minus(i, ChronoUnit.DAYS))
            .filter(d -> !availableDates.contains(d))
            .map(d ->
                new MessagesStatistics(d,
                    new Statistics(0L, 0L),
                    new Statistics(0L, 0L),
                    new Statistics(0L, 0L)))
            .collect(Collectors.toList());
        last30DaysMessagesStatics.addAll(messagesStatistics);
        last30DaysMessagesStatics.sort(Comparator.comparing(MessagesStatistics::getDate));
        return last30DaysMessagesStatics;
    }
}

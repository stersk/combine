package ua.com.tracktor.kombine.service;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ua.com.tracktor.kombine.data.RepeatedQueryRepository;
import ua.com.tracktor.kombine.data.StatisticRepository;
import ua.com.tracktor.kombine.entity.RepeatedQuery;
import ua.com.tracktor.kombine.entity.StatisticData;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@EnableAsync
public class StatisticService {
    @Autowired
    StatisticRepository statisticRepository;

    @Autowired
    RepeatedQueryRepository repeatedQueryRepository;

    private boolean collectingStarted = false;
    private AtomicInteger similarQueriesCount = new AtomicInteger();
    private Queue<Long> delayedQueriesDurationsInMillis = new ConcurrentLinkedQueue<>();

    public void logDelayedQuery(long duration) {
        if (collectingStarted) {
            delayedQueriesDurationsInMillis.add(duration);
        }
    }

    public void logSimilarQuery( String messageType, String messageToken, String messageUserId, Timestamp dateTime, Timestamp originalQueryDateTime) {
        if (collectingStarted) {
            // increase statistics counter
            similarQueriesCount.incrementAndGet();

            // record repeated query delay in milliseconds
            RepeatedQuery repeatedQuery = new RepeatedQuery();
            repeatedQuery.setDateTime(dateTime);
            repeatedQuery.setMessageType(messageType);
            repeatedQuery.setMessageToken(messageToken);
            repeatedQuery.setMessageUserId(messageUserId);
            repeatedQuery.setDelayedQueriesDuration((int)(dateTime.getTime() - originalQueryDateTime.getTime()));

            repeatedQueryRepository.save(repeatedQuery);
        }
    }

    @Scheduled(cron = "0 * * * * *")
    @Async
    void saveCollectedData() {
        if (collectingStarted) {
            int similarCount = similarQueriesCount.getAndSet(0);

            Long[] delayedDurations = delayedQueriesDurationsInMillis.toArray(new Long[0]);
            delayedQueriesDurationsInMillis.clear();


            int delayedQueryDuration = 0;
            if (delayedDurations.length != 0) {
                delayedQueryDuration = (int) Arrays.stream(ArrayUtils.toPrimitive(delayedDurations)).sum();
            }

            statisticRepository.save(new StatisticData(new Timestamp(System.currentTimeMillis()),
                    delayedDurations.length,
                    similarCount,
                    delayedQueryDuration));
        } else {
            // Start collecting data from the beginning of first minute after application started
            collectingStarted = true;
        }

    }
}

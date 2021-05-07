package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.Query;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface QueryRepository extends CrudRepository<Query, Long> {
    List<Query> findTop10ByProcessingResultCodeOrderByRequestDateDesc(int notProcessedCode);
    List<Query> findTop10ByProcessingResultCodeNotAndRetryTrueAndProcessingDateBeforeOrderByRequestDateDesc(int notProcessedCode, Timestamp lastErrorDateAfter);
    List<Query> findByProcessingResultCodeAndProcessingDateBefore(int processedCode, Timestamp startProcessedDate);
    Optional<Query> findByMessageTypeAndMessageTokenAndMessageUserId(String messageType, String messageToken, String messageUserId);
}

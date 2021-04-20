package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.Query;

import java.sql.Timestamp;
import java.util.List;

public interface QueryRepository extends CrudRepository<Query, Long> {
    List<Query> findTop10ByProcessingResultCodeOrderByRequestDateDesc(int notProcessedCode);
    List<Query> findTop10ByProcessingResultCodeNotAndRetryTrueAndProcessingDateBeforeOrderByRequestDateDesc(int notProcessedCode, Timestamp lastErrorDateAfter);
    List<Query> findByProcessingResultCodeAndProcessingDateBefore(int processedCode, Timestamp startProcessedDate);
}

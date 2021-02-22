package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.Query;

import java.util.List;

public interface QueryRepository extends CrudRepository<Query, Long> {
    List<Query> findTop10ByProcessingErrorFalseOrderByIdDesc();
}

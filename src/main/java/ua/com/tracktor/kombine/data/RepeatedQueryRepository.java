package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.RepeatedQuery;

public interface RepeatedQueryRepository extends CrudRepository<RepeatedQuery, Long> {
}

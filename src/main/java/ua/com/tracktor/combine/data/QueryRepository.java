package ua.com.tracktor.combine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.combine.entity.Query;

public interface QueryRepository extends CrudRepository<Query, Long> {
}

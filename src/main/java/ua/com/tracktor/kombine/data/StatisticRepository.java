package ua.com.tracktor.kombine.data;

import org.springframework.data.repository.CrudRepository;
import ua.com.tracktor.kombine.entity.StatisticData;

import java.sql.Timestamp;

public interface StatisticRepository extends CrudRepository<StatisticData, Timestamp> {
}

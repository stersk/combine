package ua.com.tracktor.kombine.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "statistics_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatisticData {
    @Column(name = "date_time")
    @Id
    private Timestamp dateTime;

    @Column(name = "delayed_queries_count")
    private Integer delayedQueriesCount;

    @Column(name = "similar_queries_count")
    private Integer similarQueriesCount;

    @Column(name = "delayed_queries_duration_in_ms")
    private Integer delayedQueriesDuration;
}

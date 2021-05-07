package ua.com.tracktor.kombine.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "repeated_queries")
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class RepeatedQuery {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "repeated_query_sequence"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
            }
    )
    private Long id;

    @Column(name = "date_time")
    private Timestamp dateTime;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_token")
    private String messageToken;

    @Column(name = "message_user_id")
    private String messageUserId;

    @Column(name = "query_delay_in_ms")
    private Integer delayedQueriesDuration;
}

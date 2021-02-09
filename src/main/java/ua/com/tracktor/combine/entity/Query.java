package ua.com.tracktor.combine.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "queries")
@Data
@NoArgsConstructor
public class Query {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    @GenericGenerator(
            name = "sequence-generator",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "query_sequence"),
                    @Parameter(name = "initial_value", value = "1"),
                    @Parameter(name = "increment_size", value = "1")
            }
    )

    private Long id;
    private String account;
    private String signature;
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "body")
    private String requestBody;

    @Column(name = "processing_error")
    private Boolean processingError;
}
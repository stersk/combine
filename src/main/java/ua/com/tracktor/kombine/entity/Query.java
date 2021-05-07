package ua.com.tracktor.kombine.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.springframework.http.HttpStatus;

import javax.persistence.*;
import java.sql.Timestamp;

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

    @Column(name = "request_date")
    private Timestamp requestDate;

    @Column(name = "processing_result_code")
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Integer processingResultCode;

    @Column(name = "processing_date")
    private Timestamp processingDate;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "message_token")
    private String messageToken;

    @Column(name = "message_user_id")
    private String messageUserId;

    @Column(name = "processing_error_message")
    @Type(type = "org.hibernate.type.TextType")
    private String processingErrorMessage;

    private Boolean retry;

    public void setProcessingResultCode(HttpStatus processingResultCode){
        if (processingResultCode == null) {
            this.processingResultCode = 0;
            retry           = false;
            processingError = false;
        } else {
            switch (processingResultCode.value()) {
                case (200) :
                case (418) :
                case (500) :
                    retry = false;
                    break;
                default:
                    retry = true;
                    break;
            }

            this.processingResultCode = processingResultCode.value();
            processingDate = new Timestamp(System.currentTimeMillis());
            processingError = processingResultCode != HttpStatus.OK;
        }
    }

    public HttpStatus getProcessingResultCode(){
        return (processingResultCode == null)? null : HttpStatus.valueOf(processingResultCode);
    }
}
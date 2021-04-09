package ua.com.tracktor.kombine.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneData {
    @NonNull
    private String parseResult;

    @NonNull
    @JsonProperty("isValidNumber")
    private boolean isValidNumber;

    private Boolean isValidRegionNumber;
    private String formattedNumber;
    private Integer countryCode;
    private String regionCode;
    private Long nationalNumber;
    private String validationResult;
}

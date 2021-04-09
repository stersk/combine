package ua.com.tracktor.kombine.service;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import org.springframework.stereotype.Service;
import ua.com.tracktor.kombine.entity.PhoneData;
import java.util.HashMap;
import java.util.Map;

@Service
public class PhoneUtilService {
    PhoneNumberUtil numberUtil = PhoneNumberUtil.getInstance();

    public PhoneData parsePhone(String phone) {
        PhoneData phoneData = new PhoneData();

        Map<PhoneNumberUtil.ValidationResult, String> validationResultMap = new HashMap<>();
        validationResultMap.put(PhoneNumberUtil.ValidationResult.INVALID_COUNTRY_CODE, "The number has an invalid country calling code");
        validationResultMap.put(PhoneNumberUtil.ValidationResult.INVALID_LENGTH, "The number is longer than the shortest valid numbers for this region, shorter than the longest valid numbers for this region, and does not itself have a number length that matches valid numbers for this region");
        validationResultMap.put(PhoneNumberUtil.ValidationResult.IS_POSSIBLE, "The number length matches that of valid numbers for this region");
        validationResultMap.put(PhoneNumberUtil.ValidationResult.IS_POSSIBLE_LOCAL_ONLY, "The number length matches that of local numbers for this region only");
        validationResultMap.put(PhoneNumberUtil.ValidationResult.TOO_LONG, "The number is longer than all valid numbers for this region");
        validationResultMap.put(PhoneNumberUtil.ValidationResult.TOO_SHORT, "The number is shorter than all valid numbers for this region");

        try {
            PhoneNumber number = numberUtil.parse(phone, "ZZ");

            phoneData.setParseResult("Success");
            phoneData.setValidNumber(numberUtil.isValidNumber(number));
            phoneData.setIsValidRegionNumber(numberUtil.isValidNumberForRegion(number, numberUtil.getRegionCodeForNumber(number)));
            phoneData.setFormattedNumber(numberUtil.format(number, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
            phoneData.setCountryCode(number.getCountryCode());
            phoneData.setRegionCode(numberUtil.getRegionCodeForNumber(number));
            phoneData.setNationalNumber(number.getNationalNumber());

            PhoneNumberUtil.ValidationResult validationResult = numberUtil.isPossibleNumberWithReason(number);
            phoneData.setValidationResult(validationResultMap.get(validationResult));

        } catch (NumberParseException e) {
            phoneData.setValidNumber(false);
            phoneData.setParseResult(e.getLocalizedMessage());
        }

        return phoneData;
    }
}

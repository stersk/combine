package ua.com.tracktor.kombine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.com.tracktor.kombine.entity.PhoneData;
import ua.com.tracktor.kombine.service.PhoneUtilService;

@RestController
public class PhoneUtilsController {
    @Autowired
    PhoneUtilService phoneService;

    @GetMapping(path="/phone/parse")
    public ResponseEntity<PhoneData> processRequest(@RequestParam(name = "phone") String phone){
        phone = "+" + phone;
        PhoneData phoneData = phoneService.parsePhone(phone);

        return new ResponseEntity<>(phoneData, HttpStatus.OK);
    }
}

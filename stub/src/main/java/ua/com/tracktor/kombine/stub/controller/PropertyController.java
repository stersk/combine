package ua.com.tracktor.kombine.stub.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyController {
    @GetMapping(path="/property/getAll")
    public ResponseEntity<String> getAllPropertiesRequest(){
        return getStubResponse();
    }

    @GetMapping(path="/property/get/{name}")
    public ResponseEntity<String> getPropertyRequest(@PathVariable String name){
        return getStubResponse();
    }

    @PostMapping(path= {"/property/reset/{name}", "/property/set/{name}"})
    public ResponseEntity<String> resetPropertyRequest(@PathVariable String name){
        return getStubResponse();
    }

    private ResponseEntity<String> getStubResponse() {
        StringBuilder responseBody = new StringBuilder();
        responseBody.append("This is the Viber server STUB").append(System.lineSeparator());
        responseBody.append("and this functionality not implemented.").append(System.lineSeparator());
        responseBody.append("This STUB should be used for maintenance, when main proxy server is offline");

        return new ResponseEntity<>(responseBody.toString(), HttpStatus.NOT_IMPLEMENTED);
    }
}

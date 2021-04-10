package ua.com.tracktor.kombine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.com.tracktor.kombine.service.PropertyService;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

@RestController
public class PropertyController {
    @Autowired
    PropertyService propertyService;

    @GetMapping(path="/property/getAll")
    public ResponseEntity<Map<Object,Object>> getAllPropertiesRequest(){
        return new ResponseEntity<>(propertyService.getAllProperties(), HttpStatus.OK);
    }

    @GetMapping(path="/property/get/{name}")
    public ResponseEntity<String> getPropertyRequest(@PathVariable String name){
        String result = propertyService.getProperty(name);
        if (result == null) {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @PostMapping(path="/property/set/{name}")
    public ResponseEntity<String> setPropertyRequest(@PathVariable String name, @RequestBody String value){
        try {
            boolean success = propertyService.setProperty(name, value);
            if (success) {
                return new ResponseEntity<>("", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);

            return new ResponseEntity<>(sw.toString(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path="/property/reset/{name}")
    public ResponseEntity<String> resetPropertyRequest(@PathVariable String name){
        boolean success = propertyService.resetProperty(name);
        if (success) {
            return new ResponseEntity<>("", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("", HttpStatus.NOT_FOUND);
        }
    }
}

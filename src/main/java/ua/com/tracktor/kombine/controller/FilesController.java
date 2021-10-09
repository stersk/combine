package ua.com.tracktor.kombine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.com.tracktor.kombine.service.FileService;
import ua.com.tracktor.kombine.service.UserService;

import javax.servlet.http.HttpServletRequest;

@RestController
public class FilesController {
    private final FileService fileService;
    private final UserService userService;

    @Autowired
    public FilesController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @PostMapping(path="/files/{account}/**")
    public ResponseEntity<String> uploadFile(HttpServletRequest requestEntity, @PathVariable String account) {
        if (userService.getUserDataByAccountId(account).isPresent()) {
            try {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(fileService.saveFile(requestEntity.getRequestURI().substring(6), requestEntity.getInputStream()));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body("Could not upload the file!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account with id " + account + " not found");
        }
    }

    @GetMapping(path="/files/{account}/**")
    public ResponseEntity<String> getFileRelativeAddress(HttpServletRequest requestEntity, @PathVariable String account) {
        if (userService.getUserDataByAccountId(account).isPresent()) {
            try {
                String address = fileService.getFileRelativeAddress(requestEntity.getRequestURI().substring(6));
                if (address.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("File with path " + requestEntity.getRequestURI().substring(6) + " not found");
                } else {
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(address);
                }

            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                        .body("Could not upload the file!");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Account with id " + account + " not found");
        }
    }
}

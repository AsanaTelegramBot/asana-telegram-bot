package ru.ottepel;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by savetisyan on 18/03/17
 */
@RestController
public class AsanaWebHookController {
    @RequestMapping(value = "/webhooks", method = RequestMethod.POST)
    public ResponseEntity webhooks(@RequestHeader("X-Hook-Secret") String secret) {
        System.out.println(secret);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Hook-Secret", secret);
        return new ResponseEntity(headers, HttpStatus.OK);
    }
}

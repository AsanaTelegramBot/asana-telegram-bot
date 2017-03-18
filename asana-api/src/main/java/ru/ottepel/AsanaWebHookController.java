package ru.ottepel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by savetisyan on 18/03/17
 */
//@RestController
public class AsanaWebHookController {
//
//    @Autowired
//    private AsanaTelegramBot bot;
//
//    @RequestMapping(value = "/webhooks/${chatId}", method = RequestMethod.POST, consumes = MediaType.ALL_VALUE
//    )
//    public ResponseEntity webhooks(
//            @RequestHeader(value = "X-Hook-Secret", required = false) String secret,
//            @RequestHeader(value = "X-Hook-Signature", required = false) String signature,
//            @PathVariable(value = "chatId") Long chatId) {
//        System.out.println(secret);
////        System.out.println(data);
//        try {
//            bot.sendMessage(new SendMessage().setChatId(chatId).setText("test"));
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-Hook-Secret", secret);
//        return new ResponseEntity(headers, HttpStatus.OK);
//    }
}

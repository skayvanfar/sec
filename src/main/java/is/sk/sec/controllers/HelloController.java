package is.sk.sec.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(Authentication a) {

        //        SecurityContext context = SecurityContextHolder.getContext();
        //        Authentication a = context.getAuthentication();

        return "Hello, " + a.getName() + "!";
    }
}

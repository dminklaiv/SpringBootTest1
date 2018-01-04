package dev.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@SpringBootApplication
public class Applications {
    public static void main(String[] args) throws Exception{
        SpringApplication.run(Applications.class,args);
    }

    @RequestMapping("/hello")
    public String showHelloWorld(){
        return "hello-world";
    }
}

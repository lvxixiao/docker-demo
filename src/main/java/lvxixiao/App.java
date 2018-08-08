package com.ecxpp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@SpringBootApplication
@RestController
public class App {
    @GetMapping("/")
    public String home(){
        var date = LocalDateTime.now().toLocalTime().toString();
        return String.format("msg: hello world!\ncurrent time: %s\n", date);
    }
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

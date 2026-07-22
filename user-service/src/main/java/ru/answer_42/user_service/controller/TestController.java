package ru.answer_42.user_service.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
  @GetMapping("/test")
  public String testMap(){
    return "test";
  }
  @GetMapping("/test/v2")
  public String testV2Map(){
    return "test/v2";
  }
  @GetMapping("/error")
  public String errorMap(){
    return "error";
  }
}

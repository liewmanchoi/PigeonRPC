package com.liewmanchoi.service;

import com.liewmanchoi.SayHi;
import com.liewmanchoi.User;
import com.liewmanchoi.annotation.PigeonReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author wangsheng
 * @date 2019/8/6
 */
@Slf4j
@Service
public class HelloService {
  @PigeonReference(interfaceClass = SayHi.class)
  private SayHi sayHi;

  void hello(User user) {
    log.info(">>>   开始调用HelloService#hello方法   <<<");
    System.out.println("输入的user: " + user);
    sayHi.say(user);
  }

  void helloWithResponse(User user) {
    log.info(">>>   开始调用HelloService#helloWithResponse方法   <<<");
    System.out.println("输入的user: " + user);
    User userResponse = sayHi.sayWithResponse(user);

    System.out.println("接收到的userResponse: " + userResponse);
  }
}

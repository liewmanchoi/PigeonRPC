package com.liewmanchoi.service;

import com.liewmanchoi.SayHi;
import com.liewmanchoi.User;
import com.liewmanchoi.annotation.PigeonService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangsheng
 * @date 2019/8/6
 */
@Slf4j
@PigeonService(interfaceClass = SayHi.class)
public class SayHiImpl implements SayHi {
  @Override
  public void say(User user) {
    if (user == null) {
      log.info(">>>   接收到的参数为空   <<<");
      return;
    }

    log.info(">>>   开始调用SayHi#say方法   >>>");
    System.out.println(">>>   接收到的对象为：   <<<");
    System.out.println(user.toString());
  }

  @Override
  public User sayWithResponse(User user) {
    log.info(">>>   开始调用SayHi#sayWithResponse方法   >>>");
    System.out.println(">>>   接收到的对象为：   <<<");
    System.out.println(user.toString());

    user.setId(user.getId() + 10);
    user.setName(user.getName() + " :response");

    return user;
  }
}

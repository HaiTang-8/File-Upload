package com.zq.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
public class UserController {

    @GetMapping
    public String loginPage(){
        return "Login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes attributes) {

        //获取当前的用户
        Subject subject = SecurityUtils.getSubject();

        //封装用户的登入数据
        UsernamePasswordToken token = new UsernamePasswordToken(username, DigestUtils.md5DigestAsHex(password.getBytes()));

        //执行登入方法，如果没有异常则执行成功
        try {
            subject.login(token);
            return "redirect:/file/showAll";
        } catch (UnknownAccountException e) {
            //表示用户名不存在
            attributes.addFlashAttribute("message","用户名或者密码错误");
            return "redirect:/user";
        } catch (IncorrectCredentialsException e){
            //表示密码错误
            attributes.addFlashAttribute("message","用户名或者密码错误");
            return "redirect:/user";
        }
    }
}

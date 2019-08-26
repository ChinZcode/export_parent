package cn.itcast.web.controller;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import cn.itcast.web.controller.BaseController;
import org.apache.activemq.broker.UserIDBroker;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.security.auth.Subject;
import java.security.Security;
import java.util.List;

@Controller
public class LoginController extends BaseController {

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;

    @RequestMapping("/login")
    public String login(String email,String password) {
        //1.判断用户输入的数据
        if(StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
            return "forward:/login.jsp";
        }
       /* //2.通过邮箱账号查询用户对象
        User user = userService.findByEmail(email);
        //3.比较查询的用户对象和输入的密码是否一致
        if(user != null && user.getPassword().equals(password)) {
            //4.登录成功：保存用户数据到session域中
            session.setAttribute("loginUser",user);
            List<Module> modules=moduleService.findModuleByUserId(user.getId());
            session.setAttribute("modules",modules);
            return "home/main";
        }else{
            //5.登录失败，跳转到登录页面
            request.setAttribute("error","用户名或者密码错误");
            return "forward:/login.jsp";}*/
        try {
            org.apache.shiro.subject.Subject subject = SecurityUtils.getSubject();
            UsernamePasswordToken upToken = new UsernamePasswordToken(email,password);
            subject.login(upToken);
            User user = (User) subject.getPrincipal();
            session.setAttribute("loginUser",user);
            List<Module> modules = moduleService.findModuleByUserId(user.getId());
            session.setAttribute("modules",modules);
            return "home/main";
        } catch (AuthenticationException e) {
            e.printStackTrace();
            return "forward:/login.jsp";
        }
    }


    @RequestMapping("/home")
    public String home(){
        return "home/home";
    }
    // 退出
    @RequestMapping("logout")
    public String logout(){
        // 删除session中用户
        session.removeAttribute("loginUser");
        // 销毁session
        session.invalidate();
        return "forward:/login.jsp";
    }
}

package cn.itcast.web.controller;

import cn.itcast.domain.system.User;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class BaseController {

    @Autowired
    protected HttpServletRequest request;
    @Autowired
    protected HttpServletResponse response;
    @Autowired
    protected HttpSession session;

    //模拟：企业id
    protected String getLoginCompanyId() {
        //后期实现：获取登录用户后，再获取登录用户所在的企业id
        //目前实现：数据写死
        return getLoginUser().getCompanyId();
    }


    //模拟：企业名称
    protected String getLoginCompanyName() {

        return getLoginUser().getCompanyName();
    }

    protected User getLoginUser(){
        return (User) session.getAttribute("loginUser");
    }
}
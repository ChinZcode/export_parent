package cn.itcast.service;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

public class provider {
    public static void main(String[] args)throws IOException{
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext-*.xml");
        ac.start();
        System.in.read();
    }
}

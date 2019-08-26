package cn.itcast.web.shiro;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        //1.获取登陆时候用户输入的[用户名]
        String username = (String) token.getPrincipal();

        //2.获取登陆时候用户输入的[密码]
        String password = new String((char[]) token.getCredentials());

        //3.对用户输入的密码加密、加盐。 把用户名作为盐
        String md5Password = new Md5Hash(password,username).toString();

        //4.获取认证后的正确的密码，即数据库中密码
        String dbPassword = (String) info.getCredentials();

        //5.对比
        return md5Password.equals(dbPassword);
    }
}

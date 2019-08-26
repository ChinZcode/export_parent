import org.apache.shiro.crypto.hash.Md5Hash;
import org.junit.Test;


public class text {
    @Test
    public void md5(){
        System.out.println(new Md5Hash("72a7dc98f2ce9f5312a7267d88c965d1").toString());
    }

    @Test
    public void md5salt(){
        String username = "zhangsan@export.com";
        String password = "1";
        Md5Hash md5Hash =new Md5Hash(password,username);
        System.out.println(md5Hash);
    }

}

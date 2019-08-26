package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public PageInfo<User> findByPage(String companyId, int pageNum, int PageSize) {
        PageHelper.startPage(pageNum,PageSize);
        List<User> list = userDao.findAll(companyId);
        return new PageInfo<User>(list);
    }

    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(companyId);
    }

    @Override
    public void save(User user) {
        user.setId(UUID.randomUUID().toString());
        if (user.getPassword() != null){

            String encodePwd = new Md5Hash(user.getPassword(),user.getEmail()).toString();
            user.setPassword(encodePwd);
        }
        userDao.save(user);
    }

    @Override
    public void update(User user) {
       userDao.update(user);
    }

    @Override
    public boolean delete(String id) {
       Long count = userDao.findUserRoleByUserId(id);
       if (count != null && count>0){
           return false;
       }else {userDao.delete(id);
           return true;
       }
    }

    @Override
    public User findById(String id) {
        return userDao.findById(id);
    }

    @Override
    public User findByEmail(String email) {  // 好处： 提供系统容错能力； 更符合接口设计原则（条件查询时候，为了考虑通用性，所有最好返回集合）
        List<User> list = userDao.findByEmail(email);
        return list!=null&&list.size()>0?list.get(0):null;
    }

}

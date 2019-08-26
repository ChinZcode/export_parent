package cn.itcast.service.system.impl;

import cn.itcast.dao.system.ModuleDao;
import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;
    @Autowired
    private UserDao userDao;

    public Module findById(String id) {
        return moduleDao.findById(id);
    }

    public PageInfo<Module> findByPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Module> list = moduleDao.findAll();
        return new PageInfo<Module>(list);
    }

    public void delete(String id) {
        moduleDao.delete(id);
    }

    public void save(Module module) {
        //指定id属性
        module.setId(UUID.randomUUID().toString());
        moduleDao.save(module);
    }

    public void update(Module module) {
        moduleDao.update(module);
    }

    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    @Override
    public List<Module> findModuleByRoleId(String roleId) {
        return moduleDao.findModuleByRoleId(roleId);
    }

    @Override
    public List<Module> findModuleByUserId(String userId) {
        //1.根据用户id查询用户
        User user = userDao.findById(userId);
        //2.根据用户degree级别判断
        if (user.getDegree() == 0) {
            //3.如果degree==0 （内部的sass管理）
            return moduleDao.findByBelong("0");
        }else if(user.getDegree() == 1) {
            //4.如果degree==1 （租用企业的管理员）
            return moduleDao.findByBelong("1");
        }else{
            //5.其他的用户类型
            return moduleDao.findModuleByUserId(userId);
        }

    }
}
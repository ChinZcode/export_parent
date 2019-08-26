package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    public Role findById(String id) {
        return roleDao.findById(id);
    }

    //分页
    public PageInfo<Role> findByPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Role> list = roleDao.findAll(companyId);
        return new PageInfo<Role>(list);
    }

    public void delete(String id) {
        roleDao.delete(id);
    }

    public void save(Role role) {
        //指定id属性
        role.setId(UUID.randomUUID().toString());
        roleDao.save(role);
    }

    public void update(Role role) {
        roleDao.update(role);
    }

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public List<Role> findUserRole(String userId) {
        return roleDao.findUserRole(userId);
    }
}
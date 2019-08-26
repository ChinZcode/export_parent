package cn.itcast.service.system.impl;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeptServiceImpl  implements DeptService{
    @Autowired
    private DeptDao deptDao;

    @Override
    public PageInfo<Dept> findByPage(String companyId, int pageSize, int pageNum) {
        //1.调用startPage方法
        PageHelper.startPage(pageSize,pageNum);
        //2.查询全部列表
        List<Dept> list = deptDao.findAll(companyId);
        //3.返回分页对象
        return new PageInfo<Dept>(list);
    }

    @Override
    public Dept findById(String id) {
        return deptDao.findById(id);
    }

    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findAll(companyId);
    }

    @Override
    public void save(Dept dept) {
        dept.setId(UUID.randomUUID().toString());
        deptDao.save(dept);
    }

    @Override
    public void update(Dept dept) {
        deptDao.update(dept);
    }

    @Override
    public boolean delete(String id) {
        List<Dept> list = deptDao.findDeptByParentId(id);
        if (list != null && list.size()>0){
            return false;
        }else {
            deptDao.delete(id);
            return true;
        }
    }
}

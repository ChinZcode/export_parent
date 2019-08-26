package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface ModuleService {

    //根据id查询
    Module findById(String id);

    //查询全部
    PageInfo<Module> findByPage(int pageNum, int pageSize);

    //根据id删除
    void delete(String id);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    List<Module> findModuleByRoleId(String roleId);

    List<Module> findModuleByUserId(String userId);

}
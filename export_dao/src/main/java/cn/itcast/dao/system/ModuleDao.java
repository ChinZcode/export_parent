package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {
    //根据id查询
    Module findById(String moduleId);

    //根据id删除
    void delete(String moduleId);

    //添加
    void save(Module module);

    //更新
    void update(Module module);

    //查询全部
    List<Module> findAll();

    List<Module> findModuleByRoleId(String roleId);



    List<Module> findModuleByUserId(String userId);

    List<Module> findByBelong(String belong);
}

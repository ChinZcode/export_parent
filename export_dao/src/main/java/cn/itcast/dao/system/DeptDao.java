package cn.itcast.dao.system;

import cn.itcast.domain.system.Dept;

import java.util.List;

public interface DeptDao {
    /**
     * 查询全部部门
     * @param companyId 根据企业id查询
     * @return
     */
    List<Dept> findAll(String companyId);

    /**
     * 根据id查询部门
     */
    Dept findById(String id);

    void save(Dept dept);

    void update(Dept dept);

    List<Dept> findDeptByParentId(String id);

    void delete(String id);
}

package cn.itcast.service.system;

import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface UserService {
    // 分页查询
    PageInfo<User> findByPage(String companyId, int pageNum, int PageSize);

    //查询所有部门
    List<User> findAll(String companyId);

    //保存
    void save(User user);

    //更新
    void update(User user);

    //删除
    boolean delete(String id);

    //根据id查询
    User findById(String id);

    User findByEmail(String email);
}

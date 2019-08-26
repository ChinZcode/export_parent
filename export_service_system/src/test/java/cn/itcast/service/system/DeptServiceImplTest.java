package cn.itcast.service.system;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import com.github.pagehelper.PageInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:spring/applicationContext-*.xml")
public class DeptServiceImplTest {
    // 注入service
    @Autowired
    private DeptService deptService;
    @Autowired
    private DeptDao deptDao;

    @Test
    public void findByPage(){
        PageInfo<Dept> pageInfo = deptService.findByPage("1",1,2);
        System.out.println(pageInfo);

    }
}

package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/system/dept")
public class DeptController extends BaseController{
@Autowired
    private DeptService deptService;
@RequestMapping("/list")
    public ModelAndView list(@RequestParam(defaultValue = "1")int pageNum,@RequestParam(defaultValue = "5")int pageSize){
    String companyId=getLoginCompanyId();
    PageInfo pageInfo = deptService.findByPage(companyId,pageNum,pageSize);
    ModelAndView mv = new ModelAndView();
    mv.addObject("pageInfo",pageInfo);
    mv.setViewName("system/dept/dept-list");

    return mv;
    }
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
    String companyId = getLoginCompanyId();
        List<Dept> deptList=deptService.findAll(companyId);
        ModelAndView mv= new ModelAndView();
        mv.addObject("deptLiset",deptList);
        mv.setViewName("system/dept/dept-add(1)");
        return mv;
    }
    /**
     * 新增部门
     *      1.获取表单数据构造dept对象
     *      2.添加对应的企业属性
     * 更新部门
     */
    @RequestMapping(value = "/edit" , name = "编辑部门")
    public String edit(Dept dept) {
        //模拟获取当前登录用户的数据
        //初始化当前登录用户所属的企业ID为1
        String companyId = getLoginCompanyId();
        String companyName = getLoginCompanyName();

        dept.setCompanyId(companyId);
        dept.setCompanyName(companyName);

        //1.判断是否具有id属性
        if(StringUtils.isEmpty(dept.getId())) {
            //2.没有id，保存
            deptService.save(dept);
        }else{
            //3.有id，更新
            deptService.update(dept);
        }
        return "redirect:/system/dept/list.do";
    }
    /**
     * 进入到修改界面
     * 1. 根据id进行查询
     * 2.查询所有的部门, 页面下拉列表显示
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id) {
        //模拟获取当前登录用户的数据
        //初始化当前登录用户所属的企业ID为1
        String companyId = getLoginCompanyId();

        //根据id进行查询
        Dept dept = deptService.findById(id);
        //查询所有的部门
        List<Dept> deptList = deptService.findAll(companyId);

        // 返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("dept",dept);
        mv.addObject("deptList",deptList);
        mv.setViewName("system/dept/dept-update");
        return mv;
    }
    @RequestMapping("/delete")
    @ResponseBody
    public Map<String,String>delete(String id){
        boolean flag = deptService.delete(id);
        Map<String ,String >map = new HashMap<>();
        if (flag){
            map.put("message","删除成功");
        }else {map.put("message","删除失败");}
        return map;
    }
}

package cn.itcast.web.controller.system;

import cn.itcast.domain.system.Module;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.ModuleService;
import cn.itcast.web.controller.BaseController;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping(value="/system/module")
public class ModuleController extends BaseController {

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private DeptService deptService;

    /**
     * 模块列表分页
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        //1.调用service查询部门列表
        PageInfo<Module> pageInfo = moduleService.findByPage(pageNum, pageSize);
        //2.返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("system/module/module-list");
        return mv;
    }

    /**
     * 进入新增模块页面
     *
     */
    @RequestMapping("/toAdd")
    public ModelAndView toAdd(){
        //1.查询所用模块数据，为了构造下拉框数据
        List<Module> list = moduleService.findAll();
        //2.返回
        ModelAndView mv = new ModelAndView();
        mv.addObject("menus",list);
        mv.setViewName("system/module/module-add");
        return mv;
    }

    /**
     * 新增或修改
     */
    @RequestMapping("/edit")
    public String edit(Module module) {
        //1.判断是否具有id属性
        if(StringUtils.isEmpty(module.getId())) {
            //2.没有id，保存
            moduleService.save(module);
        }else{
            //3.有id，更新
            moduleService.update(module);
        }
        return "redirect:/system/module/list.do";
    }

    /**
     * 进入到修改界面
     *  1.获取到id
     *  2.根据id进行查询
     *  3.查询所有的模块
     *  4.保存到request域中
     *  5.跳转到修改界面
     */
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        //查询所用模块数据，为了构造下拉框数据
        List<Module> list = moduleService.findAll();

        //根据id查询
        Module module = moduleService.findById(id);

        ModelAndView mv = new ModelAndView();
        mv.setViewName("system/module/module-update");
        mv.addObject("menus",list);
        mv.addObject("module",module);
        return mv;
    }

    /**
     * 删除模块
     */
    @RequestMapping("/delete")
    public String delete(String id) {
        moduleService.delete(id);
        //跳转到修改界面
        return "redirect:/system/module/list.do";
    }

}
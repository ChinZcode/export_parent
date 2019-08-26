package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController {

    @Reference
    private ContractService contractService;

    /**
     * 购销合同列表分页
     */
    @RequestMapping("/list")
    public ModelAndView list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "5") int pageSize) {
        // 构造条件
        ContractExample example = new ContractExample();
        // 构造条件-排序
        example.setOrderByClause("create_time desc");
        ContractExample.Criteria criteria = example.createCriteria();
        // 构造条件-根据公司id查询
        criteria.andCompanyIdEqualTo(getLoginCompanyId());

        /**
         * 细粒度权限控制，根据用户的degree等级判断，不同的级别显示不同的购销合同数据
         * degree  级别
         *      0-saas管理员
         *      1-企业管理员
         *      2-管理所有下属部门和人员
         *      3-管理本部门
         *      4-普通员工
         */
        User user = getLoginUser();
        if (user.getDegree() == 4){
            // 说明是普通员工： 只能查询自己创建的购销合同
            criteria.andCreateByEqualTo(user.getId());
        }
        else if (user.getDegree() == 3){
            // 说明是部门经理，可以查看本部门下所有员工创建的购销合同
            criteria.andCreateDeptEqualTo(user.getDeptId());
        }else if (user.getDegree() == 2){
            // 根据当前登陆用户的部门id，作为条件查询当前部门的子孙部门创建的购销合同。
            PageInfo<Contract> pageInfo =
                    contractService.selectByDeptId(user.getDeptId(), pageNum, pageSize);
            // 返回
            ModelAndView mv = new ModelAndView();
            mv.addObject("pageInfo",pageInfo);
            mv.setViewName("cargo/contract/contract-list");
            return mv;
        }

        //1.调用service查询购销合同列表
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example, pageNum, pageSize);
        //2.保存数据
        ModelAndView mv = new ModelAndView();
        mv.addObject("pageInfo",pageInfo);
        mv.setViewName("cargo/contract/contract-list");
        return mv;
    }

    /**
     * 进入新增页面
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "cargo/contract/contract-add";
    }

    /**
     * 新增或修改
     */
    @RequestMapping("/edit")
    public String edit(Contract contract) {
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyName());

        //1.判断是否具有id属性
        if(StringUtils.isEmpty(contract.getId())) {
            /*细粒度的权限控制*/
            // 设置创建者
            contract.setCreateBy(getLoginUser().getId());
            // 设置创建者所属部门
            contract.setCreateDept(getLoginUser().getDeptId());
            //2.没有id，保存
            contractService.save(contract);
        }else{
            //3.有id，更新
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 进入到修改界面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        //根据id进行查询
        Contract contract = contractService.findById(id);

        //保存数据
        request.setAttribute("contract",contract);
        return "cargo/contract/contract-update";
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public String delete(String id) {
        contractService.delete(id);
        //跳转到修改界面
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 购销合同（1）查看
     */
    @RequestMapping("/toView")
    public String toView(String id) {
        //根据id进行查询
        Contract contract = contractService.findById(id);
        request.setAttribute("contract",contract);
        //跳转到修改界面
        return "cargo/contract/contract-view";
    }

    /**
     * 购销合同（2）提交，将状态由0改为1
     */
    @RequestMapping("/submit")
    public String submit(String id) {
        //1.构造购销合同对象
        Contract contract = new Contract();
        //2.设置id
        contract.setId(id);
        //3.设置状态
        contract.setState(1);
        //4.更新
        contractService.update(contract);
        //跳转到修改界面
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 购销合同（3）取消，将状态由1改为0
     */
    @RequestMapping("/cancel")
    public String cancel(String id) {
        //判断
        //1.构造购销合同对象
        Contract contract = new Contract();
        //2.设置id
        contract.setId(id);
        //3.设置状态
        contract.setState(0);
        //4.更新
        contractService.update(contract);
        //跳转到修改界面
        return "redirect:/cargo/contract/list.do";
    }

}








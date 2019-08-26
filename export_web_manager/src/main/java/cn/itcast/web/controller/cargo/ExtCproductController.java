package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping(value = "/cargo/extCproduct")
public class ExtCproductController extends BaseController{
    @Reference
    private FactoryService factoryService;
    @Reference
    private ExtCproductService extCproductService;

    @RequestMapping(value = "/list")
    public String list(String contractId, String contractProductId,
                       @RequestParam(defaultValue = "1")int pageNum,@RequestParam(defaultValue = "5")int pageSize){
        //1.查询附件的生产厂家
        FactoryExample factoryExample = new FactoryExample();
        FactoryExample.Criteria criteria = factoryExample.createCriteria();
        criteria.andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        //2.查询当前货物下的所有附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        ExtCproductExample.Criteria criteria1 = extCproductExample.createCriteria();
        criteria1.andContractIdEqualTo(contractId);
        PageInfo pageInfo = extCproductService.findByPage(extCproductExample,pageNum,pageSize);
        request.setAttribute("pageInfo",pageInfo);
        //3.设置页面的基本参数：id
        request.setAttribute("contractId",contractId);
        request.setAttribute("contractProductId",contractProductId);
        return "cargo/extc/extc-list";
    }

    @RequestMapping(value = "/edit")
    public String edit(ExtCproduct extCproduct){
        extCproduct.setCompanyId(getLoginCompanyId());
        extCproduct.setCompanyName(getLoginCompanyName());
        if (StringUtils.isEmpty(extCproduct.getId())){
            extCproductService.save(extCproduct);
        }else {extCproductService.update(extCproduct);}
        return "redirect:/cargo/extCproduct/list.do?contractId="+extCproduct.getContractId()+"&contractProductId="+extCproduct.getContractProductId();
    }

    /**
     * 3. 附件修改（1）进入修改页面
     * 请求地址：http://localhost:8080/cargo/extCproduct/toUpdate.do?id=0
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //3.1 根据附件id查询
        ExtCproduct extCproduct = extCproductService.findById(id);

        //3.2 查询附件工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //3.3 保存
        request.setAttribute("extCproduct",extCproduct);
        request.setAttribute("factoryList",factoryList);

        return "cargo/extc/extc-update";
    }
    /**
     * 4. 删除附件
     * 请求地址：http://localhost:8080/cargo/extCproduct/delete.do
     * 请求参数：
     *      id                  附件id
     *      contractId          购销合同id 【为了重定向到列表】
     *      contractProductId   货物id    【为了重定向到列表】
     */
    @RequestMapping("/delete")
    public String delete(String id,String contractId,String contractProductId){
        // 调用service删除
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list.do?contractId="+
                contractId + "&contractProductId=" + contractProductId;
    }
    //上传货物
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        request.setAttribute("contractId",contractId);
        return "/cargo/product/product-import";
    }
}

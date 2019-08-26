package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.vo.ExportProductVo;
import cn.itcast.vo.ExportResult;
import cn.itcast.vo.ExportVo;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.cxf.jaxrs.client.WebClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.ws.rs.Produces;
import java.util.List;

@Controller
@RequestMapping("/cargo/export")
public class ExportController extends BaseController {
    @Reference
    private ContractService contractService;
    @Reference
    private ExportService exportService;
    @Reference
    private ExportProductService exportProductService;


    @RequestMapping("/contractList")
    public String contractList(@RequestParam(defaultValue = "1") int pageNum,
                               @RequestParam(defaultValue = "5") int pageSize){
        ContractExample example = new ContractExample();
        // 构造条件-排序
        example.setOrderByClause("create_time desc");
        ContractExample.Criteria criteria = example.createCriteria();
        // 构造条件-根据公司id查询
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        criteria.andStateEqualTo(1);

        //1.调用service查询购销合同列表
        PageInfo<Contract> pageInfo =
                contractService.findByPage(example, pageNum, pageSize);
        //2.保存数据
        request.setAttribute("pageInfo",pageInfo);
        return "cargo/export/export-contractList";
    }


    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") int pageNum,
                       @RequestParam(defaultValue = "5") int pageSize){
        ExportExample example = new ExportExample();
        ExportExample.Criteria criteria = example.createCriteria();
        criteria.andCompanyIdEqualTo(getLoginCompanyId());
        PageInfo<Export> pageInfo = exportService.findByPage(example,pageNum,pageSize);
        request.setAttribute("pageInfo",pageInfo);
        return "/cargo/export/export-list";
    }

    //根据报运单进入添加页面
    @RequestMapping("/toExport")
    public String toExport(String id){
        request.setAttribute("id",id);
        return "/cargo/export/export-toExport";
    }
    //保存报运单，新增或者修改
    @RequestMapping("/edit")
    public String edit(Export export) {
        export.setCompanyId(getLoginCompanyId());
        export.setCompanyName(getLoginCompanyName());

        //1.判断是否具有id属性
        if(StringUtils.isEmpty(export.getId())) {

            exportService.save(export);
        }else{
            //3.有id，更新
            exportService.update(export);
        }
        return "redirect:/cargo/contract/list.do";
    }
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        Export export = exportService.findById(id);
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> eps = exportProductService.findAll(epExample);
        request.setAttribute("eps",eps);
        request.setAttribute("export",export);
        return "cargo/export/export-update";

    }
    @RequestMapping("/toView")
    public String toView(String id){
        Export export = exportService.findById(id);
        request.setAttribute("export",export);
        return "/cargo/export/export-view";
    }
    @RequestMapping("/submit")
    public String submit(String id){
        Export export = new Export();
        export.setState(1);
        export.setId(id);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }
    @RequestMapping("/cancel")
    public String cancel(String id){
        Export export = new Export();
        export.setState(0);
        export.setId(id);
        exportService.update(export);
        return "redirect:/cargo/export/list.do";
    }
    @RequestMapping("/exportE")
    public String exportE(String id) {
        ExportVo exportVo = new ExportVo();
        Export export = exportService.findById(id);
        BeanUtils.copyProperties(export, exportVo);
        exportVo.setExportId(id);

        List<ExportProductVo> products = exportVo.getProducts();
        ExportProductExample epExample = new ExportProductExample();
        epExample.createCriteria().andExportIdEqualTo(id);
        List<ExportProduct> exportProductList = exportProductService.findAll(epExample);
        if (exportProductList != null && exportProductList.size() >= 0) {
            for (ExportProduct ep : exportProductList) {
                ExportProductVo epVo = new ExportProductVo();
                BeanUtils.copyProperties(ep, epVo);
                epVo.setExportId(ep.getExportId());
                epVo.setExportProductId(ep.getId());
                products.add(epVo);

            }
        }
        //7.2 电子报运(1)远程访问海关报运平台，保存报运结果到海关平台的数据库
        WebClient
                .create("http://172.20.10.4:9001/ws/export/user")
                .post(exportVo);
        //7.3 电子报运(2)远程访问海关报运平台，查询报运结果
        ExportResult exportProductResult = WebClient
                .create("http://172.20.10.4:9001/ws/export/user/" + id)
                .get(ExportResult.class);

        //7.4 修改SaasExport货代云平台的数据库： 报运单状态、备注、商品交税金额
        exportService.updateExport(exportProductResult);
       return  "redirect:/cargo/export/list.do";
    }
}

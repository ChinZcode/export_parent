package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/cargo/contractProduct")
public class ContractProductController extends BaseController{

    // 工厂service
    @Reference
    private FactoryService factoryService;
    // 货物service
    @Reference
    private ContractProductService contractProductService;

    /**
     * 1. 从购销合同列表，点击货物，进入货物列表和添加页面
     * 请求地址：http://localhost:8080/cargo/contractProduct/list.do?contractId=3
     * 存储数据：工厂、货物、..
     * 响应地址：/WEB-INF/pages/cargo/product/product-list.jsp
     */
    @RequestMapping("/list")
    public String list(String contractId,
                       @RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize){
        //1.1 查询工厂
        FactoryExample factoryExample = new FactoryExample();
        // 查询条件：工厂类型ctype = 货物
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //1.2 根据购销合同id，查询货物
        ContractProductExample cpExample = new ContractProductExample();
        // 查询条件： 购销合同id
        cpExample.createCriteria().andContractIdEqualTo(contractId);
        PageInfo<ContractProduct> pageInfo =
                contractProductService.findByPage(cpExample, pageNum, pageSize);

        //1.3 保存数据
        request.setAttribute("factoryList",factoryList);
        request.setAttribute("pageInfo",pageInfo);
        // 注意：这里需要保存购销合同id，主要是为了后面添加货物时候，要指定购销合同id
        request.setAttribute("contractId",contractId);
        //1.4 返回
        return "cargo/product/product-list";
    }
    /**
     * 2. 添加/修改货物
     */
    @RequestMapping("/edit")
    public String edit(ContractProduct contractProduct){
        // 设置部门所属企业id、名称
        contractProduct.setCompanyId(getLoginCompanyId());
        contractProduct.setCompanyName(getLoginCompanyName());
        // 判断
        if (StringUtils.isEmpty(contractProduct.getId())){

            // 添加
            contractProductService.save(contractProduct);
        } else {
            // 修改
            contractProductService.update(contractProduct);
        }
        return "redirect:/cargo/contractProduct/list.do?contractId="+contractProduct.getContractId();
    }
    /**
     * 3. 进入修改页面
     * http://localhost:8080/cargo/contractProduct/toUpdate.do?id=9
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id){
        //3.1 根据货物id查询
        ContractProduct contractProduct = contractProductService.findById(id);

        //3.2 查询货物工厂
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("货物");
        List<Factory> factoryList = factoryService.findAll(factoryExample);

        //3.3 保存
        request.setAttribute("contractProduct",contractProduct);
        request.setAttribute("factoryList",factoryList);

        return "cargo/product/product-update";
    }
    @RequestMapping("/delete")
    public String delet(String id,String contractId){
      contractProductService.delete(id);
      return "redirect:/cargo/contractProduct/list.do?contractId="+contractId;
    }
    @RequestMapping("/toImport")
    public String toImport(String contractId){
        // 保存购销合同id，因为后面上传货物，要指定对哪个购销合同添加货物
        request.setAttribute("contractId",contractId);
        return "cargo/product/product-import";
    }
    /**
     * 6. ApachePOI实现货物上传 (2) 上传   读取excel--->封装对象--->调用service保存
     * 请求参数：<input type="file" name="file">
     *
     */
    @RequestMapping("import")
    public String importExcel(String contractId, MultipartFile file) throws IOException {
        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int totalRow = sheet.getPhysicalNumberOfRows();
        for (int i = 1; i < totalRow; i++){
            Row row = sheet.getRow(i);
            ContractProduct cp = new ContractProduct();
            //4.2 创建货物对象，把excel的每一行封装为一个货物对象
            cp.setContractId(contractId); // 注意：要设置购销合同id
            cp.setFactoryName(row.getCell(1).getStringCellValue());
            cp.setProductNo(row.getCell(2).getStringCellValue());
            cp.setCnumber((int) row.getCell(3).getNumericCellValue());
            cp.setPackingUnit(row.getCell(4).getStringCellValue());
            cp.setLoadingRate(row.getCell(5).getNumericCellValue()+"");
            cp.setBoxNum((int) row.getCell(6).getNumericCellValue());
            cp.setPrice(row.getCell(7).getNumericCellValue());
            cp.setProductDesc(row.getCell(8).getStringCellValue());
            cp.setProductRequest(row.getCell(9).getStringCellValue());
            Factory factory = factoryService.findByName(cp.getFactoryName());
            if (factory != null) {
                cp.setFactoryId(factory.getId());
            }

            // 保存货物
            contractProductService.save(cp);
        }
        return "cargo/product/product-list";
        }
    }


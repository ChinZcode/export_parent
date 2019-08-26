package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Service
public class ExportServiceImpl implements ExportService {

    // 注入dao
    @Autowired
    private ExportDao exportDao;
    // 注入货物dao
    @Autowired
    private ContractProductDao contractProductDao;
    // 注入附件dao
    @Autowired
    private ExtCproductDao extCproductDao;
    // 注入购销合同dao
    @Autowired
    private ContractDao contractDao;
    // 注入商品dao
    @Autowired
    private ExportProductDao exportProductDao;
    // 注入商品附件dao
    @Autowired
    private ExtEproductDao extEproductDao;


    @Override
    public PageInfo<Export> findByPage(ExportExample ExportExample, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Export> list = exportDao.selectByExample(ExportExample);
        return new PageInfo<>(list);
    }

    @Override
    public void updateExport(ExportResult exportProductResult) {
        //1. 修改报运单
        //1.1 获取报运单id
        String exportId = exportProductResult.getExportId();

        //1.2 根据报运单id查询
        Export export = exportDao.selectByPrimaryKey(exportId);
        //1.3 设置：报运状态、备注
        export.setState(exportProductResult.getState());
        export.setRemark(exportProductResult.getRemark());
        //1.4 修改报运单
        exportDao.updateByPrimaryKeySelective(export);

        //2. 修改报运的商品
        Set<ExportProductResult> products = exportProductResult.getProducts();
        if (products != null && products.size()>0){
            for (ExportProductResult product : products) {
                // 创建商品对象
                ExportProduct exportProduct = new ExportProduct();
                // 设置报运商品id
                exportProduct.setId(product.getExportProductId());
                // 设置交税金额
                exportProduct.setTax(product.getTax());
                // 修改商品（动态更新）
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }

    }

    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    @Override
    public void save(Export Export) {

        Export.setId(UUID.randomUUID().toString());
        Export.setInputDate(new Date());
        String[] array = Export.getContractIds().split(",");
        String contractNos="";
        for (String contractId:array){
            Contract contract = contractDao.selectByPrimaryKey(contractId);
            // 获取合同号 (多个合同号以空格隔开)
            contractNos += contract.getContractNo() + " ";

            // 修改购销合同状态为2
            contract.setState(2);
            contractDao.updateByPrimaryKeySelective(contract);
        }
        // 设置合同号
        Export.setCustomerContract(contractNos);
        Map<String,String> map = new HashMap<>();
        //2.1 根据购销合同id，查询货物
        ContractProductExample cpExample = new ContractProductExample();
        cpExample.createCriteria().andContractIdIn(Arrays.asList(array));
        List<ContractProduct> cpList = contractProductDao.selectByExample(cpExample);
        //2.2 遍历货物, 构造报运的商品
        for (ContractProduct contractProduct : cpList) {//一个货物，一个商品
            //A. 创建商品对象
            ExportProduct exportProduct = new ExportProduct();
            //B. 货物--->商品。  import org.springframework.beans.BeanUtils;
            BeanUtils.copyProperties(contractProduct,exportProduct);
            //C. 设置商品属性
            exportProduct.setId(UUID.randomUUID().toString());
            exportProduct.setExportId( Export.getId());
            //D. 保存商品
            exportProductDao.insertSelective(exportProduct);

            // 存储货物id，以及对应的商品id
            map.put(contractProduct.getId(),exportProduct.getId());
        }

        //3. 保存报运的商品附件  (关键点：报运单id，每一个报运的商品id)
        // 需求： 报运的商品附件数据来源：购销合同的附件
        // select * from co_ext_cproduct where contract_id in (..)
        //3.1 根据购销合同id，查询附件
        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria()
                .andContractIdIn(Arrays.asList(array));
        List<ExtCproduct> extCproductList = extCproductDao.selectByExample(extCproductExample);
        //3.2 遍历购销合同附件，作为报运单商品附件
        for (ExtCproduct extCproduct : extCproductList) {
            //A. 创建商品附件
            ExtEproduct extEproduct = new ExtEproduct();
            //B. 货物附件 ----> 商品附件
            BeanUtils.copyProperties(extCproduct,extEproduct);
            //C. 设置商品附件属性
            extEproduct.setId(UUID.randomUUID().toString());
            // 设置报运单id
            extEproduct.setExportId(Export.getId());
            /**
             * 设置商品id
             * 已知条件： 货物id
             *          extCproduct.getContractProductId()
             * 求：      上一步保存的报运单的商品id
             */
            extEproduct.setExportProductId(map.get(extCproduct.getContractProductId()));
            //D. 保存商品附件
            extEproductDao.insertSelective(extEproduct);
        }


        //4. 保存报运单
        //4.1 设置报运单状态
        Export.setState(0);
        //4.2 设置商品数、附件数
        Export.setProNum(cpList.size());
        Export.setExtNum(extCproductList.size());
        //4.3 保存报运单
        exportDao.insertSelective(Export);
    }

    @Override
    public void update(Export Export) {

        exportDao.updateByPrimaryKeySelective(Export);
        List<ExportProduct> epList = Export.getExportProducts();
        if (epList!=null&&epList.size()>=0){
            for (ExportProduct exportProduct:epList){
                exportProductDao.updateByPrimaryKeySelective(exportProduct);
            }
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }


}

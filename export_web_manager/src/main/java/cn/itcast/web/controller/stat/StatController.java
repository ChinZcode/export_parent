package cn.itcast.web.controller.stat;

import cn.itcast.service.stat.StatService;
import cn.itcast.web.controller.BaseController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.DataOutput;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stat")
public class StatController extends BaseController{
    public StatController(){
        System.out.println(".");
    }
    @Reference
    private StatService statService;
    @RequestMapping("/toCharts")
    public String toCharts(String chartsType){

        return "stat/stat-" + chartsType;
    }

    @RequestMapping("/getFactorySale")
    @ResponseBody       // 自动把方法返回对象转换为json格式
    public List<Map<String, Object>> getFactorySale(){
        List<Map<String, Object>> factorySale =
                statService.getFactorySale(getLoginCompanyId());
        return factorySale;
    }
    /**
     * 需求2：产品销售排行，前5
     */
    @RequestMapping("/getProductSale")
    @ResponseBody       // 自动把方法返回对象转换为json格式
    public List<Map<String, Object>> getProductSale(){
        // 销售排行前5
        List<Map<String, Object>> factorySale =
                statService.getProductSale(getLoginCompanyId(),5);
        return factorySale;
    }
    @RequestMapping("/getOnline")
    @ResponseBody
    public List<Map<String,Object>>getOnline(){
        List<Map<String,Object>>factorySale=statService.getOnline();
        return factorySale;
    }
}

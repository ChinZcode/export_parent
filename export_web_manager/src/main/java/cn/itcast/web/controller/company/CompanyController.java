package cn.itcast.web.controller.company;

import cn.itcast.domain.company.Company;
import cn.itcast.service.company.CompanyService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping(value = "/company")
public class CompanyController {

    @Reference
    private CompanyService companyService;

    @RequestMapping(value="/list" , name = "企业列表")
    public String list(HttpServletRequest request) {
        List<Company> list = companyService.findAll();
        request.setAttribute("list",list);
        return "company/company-list";
    }
    @RequestMapping("/toAdd")
    public String toAdd(){
        return "company/company-add";
    }
    @RequestMapping(value="/edit")
    public String  edit(Company company){
        if (StringUtils.isEmpty(company.getId())){
            companyService.save(company);
        }
        else {
            companyService.update(company);
        }
        return "redirect:/company/list.do";
    }
    //编辑
    @RequestMapping("/toUpdate")
    public ModelAndView toUpdate(String id){
        Company company = companyService.findById(id);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("company/company-update");
        mv.addObject("company",company);
        return mv;

    }
    @RequestMapping("/delete")
    public String delete(String id){
     companyService.delete(id);
     return "redirect:/company/list.do";
    }
}

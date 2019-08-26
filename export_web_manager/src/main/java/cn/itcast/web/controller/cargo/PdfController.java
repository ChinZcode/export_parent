package cn.itcast.web.controller.cargo;

import cn.itcast.web.controller.BaseController;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.InputStream;
import java.util.HashMap;

@Controller
@RequestMapping("/cargo/export")
public class PdfController extends BaseController{
    /**
     * 1.入门案例，展示pdf
     */
    @RequestMapping("/exportPdf")
    public void exportPdf() throws Exception {
        //1. 加载jasper文件，获取文件流
        InputStream in =
                session.getServletContext().getResourceAsStream("/jasper/test01.jasper");
        //2. 创建JasperPrint对象
        //参数1:模板文件输入流；参数2：传递到模板文件中的key-value类型的参数；参数3：数据列表参数
        JasperPrint jasperPrint =
                JasperFillManager.fillReport(in,new HashMap(),new JREmptyDataSource());
        //3. 以pdf形式输出
        JasperExportManager.
                exportReportToPdfStream(jasperPrint,response.getOutputStream());
    }
}

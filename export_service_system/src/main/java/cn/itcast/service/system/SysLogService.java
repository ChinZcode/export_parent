package cn.itcast.service.system;

import cn.itcast.domain.system.SysLog;
import com.github.pagehelper.PageInfo;

public interface SysLogService {
    /**
     * 分页
     */
    PageInfo<SysLog> findByPage(String companyId, int pageNum, int pageSize);

    //保存
    void save(SysLog sysLog);
}

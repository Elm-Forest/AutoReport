package com.ctgu.autoreport.service.core.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.core.Report;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctgu.autoreport.common.constant.CommonConst.LOGIN_FAILED;
import static com.ctgu.autoreport.common.constant.CommonConst.SERVICE_ERROR;
import static java.lang.Thread.sleep;

/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:48
 */
@Service
public class ReportImpl implements Report {
    @Autowired
    private UserMapper userMapper;

    private static final Map<String, String> HEADER_MAP = new HashMap<>();

    @Override
    public void report() {
        List<User> users = userMapper.selectList(null);
        HEADER_MAP.put("Accept", "*/*");
        HEADER_MAP.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,en-GB;q=0.6,ja;q=0.5");
        HEADER_MAP.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3");
        HEADER_MAP.put("Cookie", "_hjid=d2e89115-fce8-406e-9100-43d5e0583b90; _ga=GA1.3.1877050853.1635668836; _sp_id.e13e=239c0ebd-cc15-4eb1-83cf-21d68aa5eec3.1635668335.2.1635910905.1635668824.ebf4064b-164b-4811-a5ab-d64ed9744368; _vwo_uuid=DF8316058656E6E9EF4002DA48A68BF24; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2217d238163c6518-05ec00257e12b7-a7d173c-1327104-17d238163c710e9%22%2C%22first_id%22%3A%22%22%2C%22props%22%3A%7B%7D%2C%22%24device_id%22%3A%2217d238163c6518-05ec00257e12b7-a7d173c-1327104-17d238163c710e9%22%7D; JSESSIONID=456C3D97B20DDDFF3C8FB0B148CEEEB8");
        for (User user : users) {
            try {
                HEADER_MAP.put("Referer", "http://yiqing.ctgu.edu.cn/wx/index/login.do?showWjdc=false&studentShowWjdc=false&currSchool=ctgu&CURRENT_YEAR=2019");
                HEADER_MAP.put("Origin", "Origin: http://yiqing.ctgu.edu.cn");
                ServiceDTO serviceDTO = login(user);
                System.out.println("当前用户:" + user.getUsername());
                if (!serviceDTO.getFlag()) {
                    System.out.println("登陆失败！");
                    continue;
                }
                System.out.println("登陆成功！");
                sleep(2000);
                summit();
                System.out.println("已完成请求");
                sleep(1000);
                logout();
                System.out.println("已登出！");
                sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public ServiceDTO login(User user) {
        String url = "http://yiqing.ctgu.edu.cn/wx/index/loginSubmit.do";
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("username", user.getUsername());
        paramMap.put("password", user.getPassword());
        HttpResponse result;
        try {
            result = HttpRequest.post(url).headerMap(HEADER_MAP, false).form(paramMap).timeout(30000).execute();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ServiceDTO.builder()
                    .flag(false)
                    .code(SERVICE_ERROR)
                    .message("您的账号已录入自动上报数据库，但是与安全上报服务器建立连接异常，可能是安全上报服务器目前宕机，也可能是本平台ip地址遭受安全上报服务器封锁")
                    .build();
        }
        assert result != null;
        if ("success".equals(result.body())) {
            return ServiceDTO.builder()
                    .flag(true).build();
        } else {
            return ServiceDTO.builder()
                    .flag(false)
                    .code(LOGIN_FAILED)
                    .message("您的账号登录至安全上报服务器失败，请检查重试！")
                    .build();
        }
    }

    String summit() {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ttoken", "23afa6eb-8770-408b-8f9d-95d886729b24");
        paramMap.put("province", "湖北");
        paramMap.put("city", "宜昌市");
        paramMap.put("district", "西陵区");
        paramMap.put("adcode", "443000");
        paramMap.put("longitude", "111");
        paramMap.put("latitude", "31");
        paramMap.put("sfqz", "否");
        paramMap.put("sfys", "否");
        paramMap.put("sfzy", "否");
        paramMap.put("sfgl", "否");
        paramMap.put("status", "1");
        paramMap.put("szdz", "湖北省 宜昌市 西陵区");
        paramMap.put("sjh", "15071169163");
        paramMap.put("lxrxm", "");
        paramMap.put("lxrsjh", "");
        paramMap.put("sffr", "否");
        paramMap.put("sffrAm:", "否");
        paramMap.put("sffrNoon", "否");
        paramMap.put("sffrPm", "否");
        paramMap.put("sffy", "否");
        paramMap.put("sfgr", "否");
        paramMap.put("qzglsj", "");
        paramMap.put("qzgldd", "");
        paramMap.put("glyy", "");
        paramMap.put("mqzz", "");
        paramMap.put("sffx", "否");
        paramMap.put("qt", "");
        HEADER_MAP.put("Referer", "http://yiqing.ctgu.edu.cn/wx/health/toApply.do");
        HttpResponse result = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/health/saveApply.do").headerMap(HEADER_MAP, false).form(paramMap).timeout(20000).execute();
        System.out.println(result.body());
        return result.body();
    }

    void logout() {
        HttpRequest.get("http://yiqing.ctgu.edu.cn/wx/index/logout.do");
    }
}

package com.ctgu.autoreport.service.core.impl;

import cn.hutool.core.thread.ThreadUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.common.dto.JsonDTO;
import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.common.exception.BizException;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.common.MailService;
import com.ctgu.autoreport.service.common.RedisService;
import com.ctgu.autoreport.service.core.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ctgu.autoreport.common.constant.CommonConst.*;


/**
 * @author Elm Forest
 * @date 13/10/2022 下午6:48
 */
@Service
@Log4j2
public class ReportServiceImpl implements ReportService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private MailService mailService;

    @Autowired
    private RedisService redisService;
    private static final Map<String, String> HEADER_MAP = new HashMap<>();

    ReportServiceImpl() {
        HEADER_MAP.put("Accept", "*/*");
        HEADER_MAP.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7,en-GB;q=0.6,ja;q=0.5");
        HEADER_MAP.put("User-Agent", "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2224.3");
        HEADER_MAP.put("Cookie", "_hjid=d2e89115-fce8-406e-9100-43d5e0583b90; _ga=GA1.3.1877050853.1635668836; _sp_id.e13e=239c0ebd-cc15-4eb1-83cf-21d68aa5eec3.1635668335.2.1635910905.1635668824.ebf4064b-164b-4811-a5ab-d64ed9744368; _vwo_uuid=DF8316058656E6E9EF4002DA48A68BF24; sensorsdata2015jssdkcross=%7B%22distinct_id%22%3A%2217d238163c6518-05ec00257e12b7-a7d173c-1327104-17d238163c710e9%22%2C%22first_id%22%3A%22%22%2C%22props%22%3A%7B%7D%2C%22%24device_id%22%3A%2217d238163c6518-05ec00257e12b7-a7d173c-1327104-17d238163c710e9%22%7D; JSESSIONID=456C3D97B20DDDFF3C8FB0B148CEEEB8");
    }

    @Override
    public void report() {
        log.info("开始执行该轮次");
        List<User> users = userMapper.selectList(null);
        for (User user : users) {
            ServiceDTO serviceDTO = reportCore(user);
            System.out.println(serviceDTO);
            ThreadUtil.sleep(5000);
        }
        log.info("已完成该轮次请求");
    }

    @Override
    public ServiceDTO reportCore(User user) {
        try {
            if (redisService.get(REPORTED_SUCCESS + user.getUsername()) != null) {
                throw new BizException("今日已上报");
            }
            HEADER_MAP.put("Referer", "http://yiqing.ctgu.edu.cn/wx/index/login.do?showWjdc=false&studentShowWjdc=false&currSchool=ctgu&CURRENT_YEAR=2019");
            HEADER_MAP.put("Origin", "Origin: http://yiqing.ctgu.edu.cn");
            ServiceDTO serviceDTO = login(user);
            System.out.println("当前用户:" + user.getUsername());
            if (!serviceDTO.getFlag()) {
                String msg = "登陆失败：";
                if (serviceDTO.getCode().equals(LOGIN_FAILED)) {
                    autoDelete(user.getUsername());
                    msg = msg + "已自动删除" + user;
                } else {
                    msg = msg + "服务器未应答";
                }
                throw new BizException(msg);
            }
            System.out.println("登陆成功！");
            ThreadUtil.sleep(1000);
            ServiceDTO tokenService = getToken(getFormList());
            if (!tokenService.getFlag()) {
                throw new BizException(tokenService.getMessage());
            }
            String token = tokenService.getData();
            System.out.println(tokenService.getMessage());
            List<JsonDTO> historyList = getHistoryList();
            String summit;
            try {
                summit = summit(token, historyList.get(2));
            } catch (Exception e) {
                throw new BizException("summit失败:" + e.getMessage());
            }
            System.out.println(summit);
            System.out.println("已完成请求");
            ThreadUtil.sleep(1000);
            logout();
            System.out.println("已登出！");
            redisService.set(REPORTED_SUCCESS + user.getUsername(), user.getUsername());
            return ServiceDTO.builder()
                    .flag(false)
                    .message("已成功完成安全上报")
                    .data(summit)
                    .build();
        } catch (Exception e) {
            return ServiceDTO.builder()
                    .flag(false)
                    .message(e.getMessage())
                    .build();
        }
    }


    void autoDelete(String username) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        try {
            mailService.sendMail(EmailDTO.builder().email(user.getEmail()).subject("CTGU自动安全上报系统账户移除通知").content("您的账号" + user.getUsername() + "<br>登录至安全上报服务器失败，现已被移除</br>" + "如果您向继续使用，请访问以下地址进行重新注册:<br>http://120.25.3.27</br>" + "<br>如果您决定停止使用，系统已自动删除有关您的所有信息</br>" + "<br>本系统开源且完全非盈利，感谢使用，欢迎关注作者的GitHub主页：https://github.com/Elm-Forest</br>").build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
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
                    .message("您的账号已录入自动上报数据库，但是与安全上报服务器建立连接异常，可能是安全上报服务器目前宕机，也可能是本平台ip地址遭受安全上报服务器封锁").build();
        }
        assert result != null;
        if ("success".equals(result.body())) {
            return ServiceDTO.builder().flag(true).build();
        } else {
            return ServiceDTO.builder().flag(false).code(LOGIN_FAILED).message("您的账号登录至安全上报服务器失败，请检查重试！").build();
        }
    }

    ServiceDTO getToken(String body) {
        String token = ReUtil.get("<input type=\"hidden\" name=\"ttoken\" value=\"(.*?)\"/", body, 1);
        ServiceDTO serviceDTO;
        if (token != null) {
            serviceDTO = ServiceDTO.builder()
                    .flag(true)
                    .message("随机token:" + token)
                    .data(token)
                    .build();
        } else {
            serviceDTO = ServiceDTO.builder()
                    .flag(false)
                    .message("自动上报时抓取token失败")
                    .data(null)
                    .build();
        }
        return serviceDTO;
    }

    public String getFormList() {
        String url = "http://yiqing.ctgu.edu.cn/wx/health/toApply.do";
        HttpResponse execute = HttpRequest.get(url).headerMap(HEADER_MAP, false).timeout(30000).execute();
        return execute.body();
    }

    public List<JsonDTO> getHistoryList() {
        String url = "http://yiqing.ctgu.edu.cn/wx/health/studentHis.do";
        String historyList = HttpRequest.post(url).execute().body();
        JSONArray array = JSONUtil.parseArray(historyList);
        return JSONUtil.toList(array, JsonDTO.class);
    }

    String summit(String token, JsonDTO jsonDTO) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ttoken", token);
        paramMap.put("province", jsonDTO.getProvince());
        paramMap.put("city", jsonDTO.getCity());
        paramMap.put("district", jsonDTO.getDistrict());
        paramMap.put("adcode", jsonDTO.getAdcode());
        paramMap.put("longitude", "0");
        paramMap.put("latitude", "0");
        paramMap.put("sfqz", "否");
        paramMap.put("sfys", "否");
        paramMap.put("sfzy", "否");
        paramMap.put("sfgl", "否");
        paramMap.put("status", "1");
        paramMap.put("szdz", jsonDTO.getSzdz());
        paramMap.put("sjh", jsonDTO.getSjh());
        paramMap.put("lxrxm", jsonDTO.getLxrxm());
        paramMap.put("lxrsjh", jsonDTO.getLxrsjh());
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
        System.out.println(paramMap);
        HEADER_MAP.put("Referer", "http://yiqing.ctgu.edu.cn/wx/health/toApply.do");
        HttpResponse result = HttpRequest.post("http://yiqing.ctgu.edu.cn/wx/health/saveApply.do").headerMap(HEADER_MAP, false).form(paramMap).timeout(20000).execute();
        return result.body();
    }

    void logout() {
        HttpRequest.get("http://yiqing.ctgu.edu.cn/wx/index/logout.do");
    }
}

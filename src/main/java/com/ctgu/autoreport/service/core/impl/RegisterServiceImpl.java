package com.ctgu.autoreport.service.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.common.enums.StatusCodeEnum;
import com.ctgu.autoreport.common.exception.BizException;
import com.ctgu.autoreport.common.utils.AesUtils;
import com.ctgu.autoreport.common.vo.Result;
import com.ctgu.autoreport.common.vo.UserVO;
import com.ctgu.autoreport.dao.UserMapper;
import com.ctgu.autoreport.entity.User;
import com.ctgu.autoreport.service.common.MailService;
import com.ctgu.autoreport.service.common.RedisService;
import com.ctgu.autoreport.service.core.RegisterService;
import com.ctgu.autoreport.service.core.ReportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.ctgu.autoreport.common.constant.CommonConst.*;
import static com.ctgu.autoreport.common.enums.StatusCodeEnum.SYSTEM_ERROR;

/**
 * @author Elm Forest
 * @date 14/10/2022 下午3:05
 */
@Service
@Log4j2
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisService redisService;

    @Autowired
    private MailService mailService;

    @Autowired
    private AesUtils aesUtils;

    @Autowired
    private ReportService reportService;

    private final String regLgSvcEorMsg;
    private final String regLgSecMsg;
    private final String delMoMsg;

    public RegisterServiceImpl() throws IOException {
        InputStream stream = getClass().getResourceAsStream("/message.properties");
        Properties properties = new Properties();
        properties.load(stream);
        regLgSvcEorMsg = properties.getProperty("regLgSvcEorMsg");
        regLgSecMsg = properties.getProperty("regLgSecMsg");
        delMoMsg = properties.getProperty("delMoMsg");
        assert stream != null;
        stream.close();
    }

    @Override
    public Result<?> register(UserVO userVO) {
        if (redisService.get(AUTO_REPORT + userVO.getUsername()) != null) {
            return Result.fail("请勿重复提交！");
        }
        redisService.set(AUTO_REPORT + userVO.getUsername(), userVO.getUsername(), 60);
        Result<?> result = registerCore(userVO);

        redisService.del(AUTO_REPORT + userVO.getUsername());
        return result;
    }

    @Override
    public Result<?> delete(UserVO userVO) {
        if (redisService.get(AUTO_REPORT + userVO.getUsername()) != null) {
            return Result.fail("请勿重复请求！");
        }
        redisService.set(AUTO_REPORT + userVO.getUsername(), userVO.getUsername(), 60);
        Result<?> result = deleteCore(userVO);
        redisService.del(AUTO_REPORT + userVO.getUsername());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    Result<?> deleteCore(UserVO userVO) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, userVO.getUsername()));
        log.warn("删除用户: " + user);
        if (user == null) {
            return Result.fail(StatusCodeEnum.NO_EXISTED);
        }
        try {
            mailService.sendMail(EmailDTO.builder()
                    .email(user.getEmail())
                    .subject("自动安全上报系统账户移除通知")
                    .content("您的账号" + user.getUsername() + delMoMsg)
                    .build());
        } catch (MessagingException e) {
            log.error("邮件服务发生异常: " + e.getMessage());
        }
        try {
            int delete = userMapper.delete(new LambdaQueryWrapper<User>().eq(User::getUsername, userVO.getUsername()));
            if (delete <= 0) {
                throw new BizException("删除失败，您的账户可能已经被移除");
            }
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
        return Result.ok(null, "您的账户已成功移除，本平台已删除有关您的所有信息，已向您的邮箱发送邮件告知");
    }

    @Transactional(rollbackFor = Exception.class)
    Result<?> registerCore(UserVO userVO) {
        if (!checkExists(userVO.getUsername())) {
            return Result.fail(StatusCodeEnum.EXISTED);
        }
        log.info("注册用户:" + userVO.getUsername());
        User user = User.builder()
                .username(userVO.getUsername())
                .password(aesUtils.encryptAes(userVO.getPassword()))
                .email(userVO.getEmail())
                .build();
        ServiceDTO serviceDTO = reportService.login(user);
        if (!serviceDTO.getFlag()) {
            if (serviceDTO.getCode().equals(LOGIN_FAILED)) {
                return Result.fail(serviceDTO.getMessage());
            } else if (serviceDTO.getCode().equals(SERVICE_ERROR)) {
                try {
                    int insert = userMapper.insert(user);
                    if (insert <= 0) {
                        throw new BizException("注册发生异常,user字段插入失败:insert <= 0");
                    }
                    mailService.sendMail(EmailDTO.builder()
                            .email(user.getEmail())
                            .subject("欢迎注册自动安全上报系统")
                            .content("您的账号" + user.getUsername() + regLgSvcEorMsg)
                            .build());
                } catch (Exception e) {
                    log.error(e.getMessage());
                    return Result.fail(SYSTEM_ERROR);
                }
                return Result.fail(serviceDTO.getMessage());
            }
        }
        try {
            int insert = userMapper.insert(user);
            if (insert <= 0) {
                throw new BizException("注册发生异常,user字段插入失败:insert <= 0");
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Result.fail(SYSTEM_ERROR);
        }
        try {
            mailService.sendMail(EmailDTO.builder()
                    .email(user.getEmail())
                    .subject("欢迎注册自动安全上报系统")
                    .content("您的账号" + user.getUsername() + regLgSecMsg)
                    .build());
        } catch (Exception e) {
            log.error("邮件服务抛出异常:" + e.getMessage());
        }
        serviceDTO = reportService.reportCore(user);
        String msg = "已完成注册，";
        if (serviceDTO.getFlag()) {
            redisService.set(REPORTED_NUMS, (int) redisService.get(REPORTED_NUMS) - 1);
            msg += "当前已上报";
        } else {
            msg += "但当前上报未能完成，可能原因是此账号今日已自主上报";
        }
        log.info(userVO.getUsername() + msg + serviceDTO);
        return Result.ok(null, msg);
    }

    boolean checkExists(String username) {
        Object user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return user == null;
    }
}

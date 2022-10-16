package com.ctgu.autoreport.service.core.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.common.dto.ServiceDTO;
import com.ctgu.autoreport.common.enums.StatusCodeEnum;
import com.ctgu.autoreport.common.exception.BizException;
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
    private ReportService reportService;

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
        System.out.println(user);
        if (user == null) {
            return Result.fail(StatusCodeEnum.NO_EXISTED);
        }
        try {
            mailService.sendMail(EmailDTO.builder()
                    .email(user.getEmail())
                    .subject("CTGU自动安全上报系统账户移除通知")
                    .content("您的账号" + user.getUsername() + "已被移除，本平台已删除有关您的所有信息<br>本系统开源且完全非盈利，感谢使用，欢迎关注作者的GitHub主页：https://github.com/Elm-Forest</br>")
                    .build());
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
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
        User user = User.builder()
                .username(userVO.getUsername())
                .password(userVO.getPassword())
                .email(userVO.getEmail())
                .build();
        ServiceDTO serviceDTO = checkLogin(user);
        if (!serviceDTO.getFlag()) {
            if (serviceDTO.getCode().equals(LOGIN_FAILED)) {
                return Result.fail(serviceDTO.getMessage());
            } else if (serviceDTO.getCode().equals(SERVICE_ERROR)) {
                try {
                    int insert = userMapper.insert(user);
                    if (insert <= 0) {
                        throw new BizException("注册异常");
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return Result.fail(SYSTEM_ERROR);
                }
                return Result.fail(serviceDTO.getMessage());
            }
        }
        try {
            int insert = userMapper.insert(user);
            if (insert <= 0) {
                throw new BizException("注册异常");
            }
        } catch (Exception e) {
            return Result.fail(SYSTEM_ERROR);
        }
        try {
            mailService.sendMail(EmailDTO.builder()
                    .email(user.getEmail())
                    .subject("欢迎注册CTGU自动安全上报系统")
                    .content("您的账号" + user.getUsername() + "<br>已成功登录至安全上报服务器，并完成本系统注册</br>" +
                            "注意：本系统仅用于学习用途，您须保证您完全遵守疫情防控规定，在使用本系统期间您必须处在校内，如果您离校，请主动删除账号" +
                            "<br>继续使用即代表您同意该条约，由于个人原因造成疫情扩散等恶劣情景，作者不承担任何责任</br>" +
                            "<br>本系统完全保护且不收集您的个人信息，数据采用AES对称加密算法存储。如果您决定停止使用，系统会删除有关您的所有信息</br>" +
                            "<br>本系统开源且完全非盈利，开源地址：https://github.com/Elm-Forest/AutoReport</br>" +
                            "欢迎关注作者的GitHub主页：https://github.com/Elm-Forest，如您有数据挖掘和深度学习任务的协助需求，欢迎通过本邮箱联系作者，感谢使用")
                    .build());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        serviceDTO = reportService.reportCore(user);
        String msg = "已完成注册，";
        if (serviceDTO.getFlag()) {
            msg += "今日已上报";
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

    ServiceDTO checkLogin(User user) {
        return new ReportServiceImpl().login(user);
    }
}

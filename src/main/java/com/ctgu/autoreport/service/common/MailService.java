package com.ctgu.autoreport.service.common;


import com.ctgu.autoreport.common.dto.EmailDTO;
import jakarta.mail.MessagingException;


/**
 * @author Elm Forest
 */
public interface MailService {
    /**
     * 发送自定义邮件，异步任务
     *
     * @param emailDTO 收件对象
     * @throws MessagingException 消息异常
     */
    void sendMail(EmailDTO emailDTO) throws MessagingException;

    /**
     * 发送同步邮件
     *
     * @param emailDTO 收件对象
     * @throws MessagingException 消息异常
     */
    void sendMailWithSync(EmailDTO emailDTO) throws MessagingException;
}

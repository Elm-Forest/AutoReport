package com.ctgu.autoreport.service.common;




import com.ctgu.autoreport.common.dto.EmailDTO;

import javax.mail.MessagingException;


/**
 * @author Elm Forest
 */
public interface MailService {
    /**
     * 发送自定义邮件
     *
     * @param emailDTO 收件对象
     * @throws MessagingException 消息异常
     */
    void sendMail(EmailDTO emailDTO) throws MessagingException;
}

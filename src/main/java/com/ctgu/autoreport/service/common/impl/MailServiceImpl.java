package com.ctgu.autoreport.service.common.impl;

import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.service.common.MailService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


/**
 * @author Elm Forest
 */
@Data
@Service
@EnableAsync
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String hostMail;

    @Override
    @Async
    public void sendMail(EmailDTO emailDTO) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(emailDTO.getSubject());
        helper.setText(emailDTO.getContent(), true);
        helper.setTo(emailDTO.getEmail());
        helper.setFrom(hostMail);
        System.out.println("正在发送邮件至" + emailDTO.getEmail());
        mailSender.send(mimeMessage);
        System.out.println("已发送");
    }
}

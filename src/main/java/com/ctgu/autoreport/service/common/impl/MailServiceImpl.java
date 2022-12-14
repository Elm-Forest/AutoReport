package com.ctgu.autoreport.service.common.impl;

import com.ctgu.autoreport.common.dto.EmailDTO;
import com.ctgu.autoreport.service.common.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;


/**
 * @author Elm Forest
 */
@Data
@Service
@EnableAsync
@Log4j2
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSenderImpl mailSender;

    @Value("${spring.mail.username}")
    private String hostMail;

    @Override
    @Async
    public void sendMail(EmailDTO emailDTO) throws MessagingException {
        sendCore(emailDTO);
    }

    @Override
    public void sendMailWithSync(EmailDTO emailDTO) throws MessagingException {
        sendCore(emailDTO);
    }

    private void sendCore(EmailDTO emailDTO) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setSubject(emailDTO.getSubject());
        helper.setText(emailDTO.getContent(), true);
        helper.setTo(emailDTO.getEmail());
        helper.setFrom(hostMail);
        log.info("正在发送邮件至" + emailDTO.getEmail());
        mailSender.send(mimeMessage);
        log.info("已发送");
    }

}

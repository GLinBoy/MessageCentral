package com.glinboy.app.config.dev;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
@Profile("dev")
public class EmbeddedMailConfig {

    private final Logger log = LoggerFactory.getLogger(EmbeddedMailConfig.class);

    private GreenMail greenMail;

    @Value("${spring.mail.port}")
    private Integer port;

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.protocol}")
    private String protocol;

    @PostConstruct
    public void setupMailServer() {
        if (greenMail == null || !greenMail.isRunning()) {
            greenMail = new GreenMail(new ServerSetup(port, host, protocol));
            greenMail.start();
            log.info("Mail Server Started");
        } else {
            log.info("Server is up!");
        }
    }

    @PreDestroy
    public void stopMailServer() {
        greenMail.stop();
        log.info("Mail Server Stoped");
    }

}

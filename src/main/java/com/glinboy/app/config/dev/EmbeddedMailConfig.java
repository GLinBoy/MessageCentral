package com.glinboy.app.config.dev;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

@Component
@Profile("dev")
public class EmbeddedMailConfig {

	private final Logger log = LoggerFactory.getLogger(EmbeddedMailConfig.class);

	private GreenMail greenMail;
	
	private Integer port = 2525;
	
	private String host = "localhost";
	
	private String protocol = "smtp";

	@PostConstruct
	public void setupMailServer() {
		greenMail = new GreenMail(new ServerSetup(port, host, protocol));
		greenMail.start();
		log.info("Mail Server Started");
	}

	public void stopMailServer() {
		greenMail.stop();
		log.info("Mail Server Stoped");
	}

}

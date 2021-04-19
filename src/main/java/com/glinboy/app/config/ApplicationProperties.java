package com.glinboy.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Message Central.
 * <p>
 * Properties are configured in the {@code application.yml} file. See
 * {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {

	private final Email email = new Email();

	public Email getEmail() {
		return email;
	}

	public static class Email {
		private String from;

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

	}
}

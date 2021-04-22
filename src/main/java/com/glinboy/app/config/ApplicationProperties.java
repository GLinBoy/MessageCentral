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
	
	private final SMS sms = new SMS();

	private final Credential credential = new Credential();

	public Email getEmail() {
		return email;
	}

	public SMS getSms() {
		return sms;
	}

	public Credential getCredential() {
		return credential;
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

	public static class SMS {

		private String from;

		public String getFrom() {
			return from;
		}

		public void setFrom(String from) {
			this.from = from;
		}

	}

	public static class Credential {

		private final Twilio twilio = new Twilio();
		private final Kavenegar kavenegar = new Kavenegar();

		public Twilio getTwilio() {
			return twilio;
		}

		public Kavenegar getKavenegar() {
			return kavenegar;
		}

		public static class Twilio {

			private String accountSid;
			private String token;

			public String getAccountSid() {
				return accountSid;
			}

			public void setAccountSid(String accountSid) {
				this.accountSid = accountSid;
			}

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}
		}
		
		public static class Kavenegar {
			
			private String token;

			public String getToken() {
				return token;
			}

			public void setToken(String token) {
				this.token = token;
			}
		}
	}
}

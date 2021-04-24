package com.glinboy.app.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
@Profile("!test")
public class FirebaseConfig {

	@Value("${application.notification.firebase.file-path}")
	private String filePath;

	@Bean
	FirebaseMessaging firebaseMessaging() throws IOException {
		GoogleCredentials googleCredentials = GoogleCredentials
				.fromStream(new ClassPathResource(filePath)
						.getInputStream());
		FirebaseOptions firebaseOptions = FirebaseOptions.builder()
				.setCredentials(googleCredentials)
				.build();
		FirebaseApp app = FirebaseApp.getApps().stream().
				filter(fa -> fa.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
				.findAny()
				.orElseGet(() -> FirebaseApp.initializeApp(firebaseOptions, FirebaseApp.DEFAULT_APP_NAME));
		return FirebaseMessaging.getInstance(app);
	}
}

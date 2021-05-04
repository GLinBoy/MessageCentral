package com.glinboy.app.service.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.collections.MapUtils;

/**
 * A DTO for the {@link com.glinboy.app.domain.Notification} entity.
 */
public class NotificationsDTO implements Serializable {

	private Map<String, String> receivers = new HashMap<>();

	@NotNull
	@Size(max = 128)
	private String subject;

	@NotNull
	@Size(max = 4000)
	private String content;

	@Size(max = 256)
	private String image;

	private Set<NotificationDataDTO> data = new HashSet<>();

	public Map<String, String> getReceivers() {
		return receivers;
	}

	public void setReceivers(Map<String, String> receivers) {
		this.receivers = receivers;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Set<NotificationDataDTO> getData() {
		return data;
	}

	public void setData(Set<NotificationDataDTO> data) {
		this.data = data;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof NotificationsDTO)) {
			return false;
		}

		NotificationsDTO notificationsDTO = (NotificationsDTO) o;

		return !MapUtils.isEmpty(this.receivers) && 
				this.receivers.size() == notificationsDTO.receivers.size()
				&& this.receivers.entrySet().containsAll(notificationsDTO.receivers.entrySet())
				&& (this.subject == null ? notificationsDTO.subject == null : this.subject.equals(notificationsDTO.subject))
				&& (this.content == null ? notificationsDTO.content == null : this.content.equals(notificationsDTO.content));
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.receivers.keySet().stream()
				.map(key -> key + "=" + getReceivers().get(key))
				.collect(Collectors.joining(", ", "{", "}")));
	}

	// prettier-ignore
	@Override
	public String toString() {
		return "NotificationDTO{" + "receivers=" + getReceivers()
				.keySet().stream()
					.map(key -> key + "=" + getReceivers().get(key))
					.collect(Collectors.joining(", ", "{", "}")) +
				", subject='" + getSubject() + "'" +
				", content='" + getContent() + "'" +
				", image='" + getImage() + "'" +
				", data='"
				+ getData().stream().map(NotificationDataDTO::toString)
				.collect(Collectors.joining(", ")) + "'" + "}";
	}
}

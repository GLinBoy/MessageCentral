package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.repository.NotificationRepository;
import com.glinboy.app.security.AuthoritiesConstants;
import com.glinboy.app.service.NotificationChannelService;
import com.glinboy.app.service.dto.NotificationDTO;
import com.glinboy.app.service.dto.NotificationsDTO;
import com.glinboy.app.service.mapper.NotificationMapper;

/**
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationResourceIT {

    private static final String DEFAULT_USERNAME = "AAAAAAAAAA";
    private static final String UPDATED_USERNAME = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE = "BBBBBBBBBB";

    private static final String DEFAULT_KEY = "AAAAAAAAAA";
    private static final String DEFAULT_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
	private static final String ENTITY_API_URL_MULTIPLE = "/api/notifications/multiple";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationMockMvc;

    @Mock
    private NotificationChannelService<NotificationDTO> notificationProviderService;

    private Notification notification;

    private NotificationData notificationData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity(EntityManager em) {
        Notification notification = new Notification()
            .username(DEFAULT_USERNAME)
            .token(DEFAULT_TOKEN)
            .subject(DEFAULT_SUBJECT)
            .content(DEFAULT_CONTENT)
            .image(DEFAULT_IMAGE);
        return notification;
    }

    /**
     * Create multiple entities for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static List<NotificationsDTO> createNotificationsDTO(int notificationsDTOCount, int reciversCount) {
        List<NotificationsDTO> notificationsDTO = IntStream.range(0, notificationsDTOCount)
                .mapToObj(i -> {
                    Map<String, String> rs = new HashMap<>();
                    for (int j = 1; j <= reciversCount; j++) {
                        rs.put("USER_" + j, "TOKEN_" + j);
                    }
                    NotificationsDTO n = new NotificationsDTO();
                    n.setReceivers(rs);
                    n.setSubject(String.format("SUBJECT_%d", i));
                    n.setContent(String.format("CONETNT_%d", i));
                    return n;
                })
                .collect(Collectors.toList());
        assertThat(notificationsDTO.size()).isEqualTo(notificationsDTOCount);
        notificationsDTO.forEach(es -> assertThat(es.getReceivers().size()).isEqualTo(reciversCount));
        return notificationsDTO;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity(EntityManager em) {
        Notification notification = new Notification()
            .username(UPDATED_USERNAME)
            .token(UPDATED_TOKEN)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE);
        return notification;
    }

    @BeforeEach
    public void initTest() {
        notification = createEntity(em);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void createNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        doNothing().when(notificationProviderService).sendMessage(notificationDTO);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testNotification.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotification.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    void failedCreateNotification() throws Exception {
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        doNothing().when(notificationProviderService).sendMessage(notificationDTO);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void createBulkNotification() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        // Create multiple Notifications
        int notificationsCount = 2;
        int reciverCount = 5;
        List<NotificationsDTO> notificationsDTOs = createNotificationsDTO(notificationsCount, reciverCount);
        doNothing().when(notificationProviderService).sendMessage(new NotificationDTO[0]);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationsDTOs))
            )
            .andExpect(status().isOk());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + (notificationsCount * reciverCount));
    }

    @Test
    @Transactional
    void failedCreateBulkNotification() throws Exception {
        // Create multiple Notifications
        int notificationsCount = 2;
        int reciverCount = 5;
        List<NotificationsDTO> notificationsDTOs = createNotificationsDTO(notificationsCount, reciverCount);
        doNothing().when(notificationProviderService).sendMessage(new NotificationDTO[0]);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationsDTOs))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    /**
     * Added for issue:
     * https://github.com/GLinBoy/MessageCentral/issues/42
     * @throws Exception
     */
    void createNotificationWithData() throws Exception {
        int databaseSizeBeforeCreate = notificationRepository.findAll().size();
        // Create Data
        notificationData = new NotificationData();
        notificationData.setKey(DEFAULT_KEY);
        notificationData.setValue(DEFAULT_VALUE);
        notification.setData(Set.of(notificationData));
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        doNothing().when(notificationProviderService).sendMessage(notificationDTO);
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isCreated());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate + 1);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testNotification.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotification.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testNotification.getData()).isNotEmpty();
        assertThat(testNotification.getData().size()).isEqualTo(1);
        assertThat(testNotification.getData().stream().anyMatch(d -> d.getKey().equals(DEFAULT_KEY))).isTrue();
        assertThat(testNotification.getData().stream().anyMatch(d -> d.getValue().equals(DEFAULT_VALUE))).isTrue();
        assertThat(testNotification.getData().stream().anyMatch(d -> d.getId() == null)).isFalse();
        assertThat(testNotification.getData().stream().anyMatch(d -> d.getNotification() == null)).isFalse();
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        int databaseSizeBeforeCreate = notificationRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        // set the field null
        notification.setUsername(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        // set the field null
        notification.setToken(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        // set the field null
        notification.setSubject(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationRepository.findAll().size();
        // set the field null
        notification.setContent(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        restNotificationMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isBadRequest());

        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotifications() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get the notification
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL_ID, notification.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notification.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.image").value(DEFAULT_IMAGE));
    }

    @Test
    @Transactional
    void failedGetNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isForbidden());

            // Get the notification
            restNotificationMockMvc
                .perform(get(ENTITY_API_URL_ID, notification.getId()))
                .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getNotificationsByIdFiltering() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        Long id = notification.getId();

        defaultNotificationShouldBeFound("id.equals=" + id);
        defaultNotificationShouldNotBeFound("id.notEquals=" + id);

        defaultNotificationShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.greaterThan=" + id);

        defaultNotificationShouldBeFound("id.lessThanOrEqual=" + id);
        defaultNotificationShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username equals to DEFAULT_USERNAME
        defaultNotificationShouldBeFound("username.equals=" + DEFAULT_USERNAME);

        // Get all the notificationList where username equals to UPDATED_USERNAME
        defaultNotificationShouldNotBeFound("username.equals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username not equals to DEFAULT_USERNAME
        defaultNotificationShouldNotBeFound("username.notEquals=" + DEFAULT_USERNAME);

        // Get all the notificationList where username not equals to UPDATED_USERNAME
        defaultNotificationShouldBeFound("username.notEquals=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username in DEFAULT_USERNAME or UPDATED_USERNAME
        defaultNotificationShouldBeFound("username.in=" + DEFAULT_USERNAME + "," + UPDATED_USERNAME);

        // Get all the notificationList where username equals to UPDATED_USERNAME
        defaultNotificationShouldNotBeFound("username.in=" + UPDATED_USERNAME);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username is not null
        defaultNotificationShouldBeFound("username.specified=true");

        // Get all the notificationList where username is null
        defaultNotificationShouldNotBeFound("username.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username contains DEFAULT_USERNAME
        defaultNotificationShouldBeFound("username.contains=" + DEFAULT_USERNAME);

        // Get all the notificationList where username contains UPDATED_USERNAME
        defaultNotificationShouldNotBeFound("username.contains=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByUsernameNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where username does not contain DEFAULT_USERNAME
        defaultNotificationShouldNotBeFound("username.doesNotContain=" + DEFAULT_USERNAME);

        // Get all the notificationList where username does not contain UPDATED_USERNAME
        defaultNotificationShouldBeFound("username.doesNotContain=" + UPDATED_USERNAME);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token equals to DEFAULT_TOKEN
        defaultNotificationShouldBeFound("token.equals=" + DEFAULT_TOKEN);

        // Get all the notificationList where token equals to UPDATED_TOKEN
        defaultNotificationShouldNotBeFound("token.equals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token not equals to DEFAULT_TOKEN
        defaultNotificationShouldNotBeFound("token.notEquals=" + DEFAULT_TOKEN);

        // Get all the notificationList where token not equals to UPDATED_TOKEN
        defaultNotificationShouldBeFound("token.notEquals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token in DEFAULT_TOKEN or UPDATED_TOKEN
        defaultNotificationShouldBeFound("token.in=" + DEFAULT_TOKEN + "," + UPDATED_TOKEN);

        // Get all the notificationList where token equals to UPDATED_TOKEN
        defaultNotificationShouldNotBeFound("token.in=" + UPDATED_TOKEN);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token is not null
        defaultNotificationShouldBeFound("token.specified=true");

        // Get all the notificationList where token is null
        defaultNotificationShouldNotBeFound("token.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token contains DEFAULT_TOKEN
        defaultNotificationShouldBeFound("search=token:*" + DEFAULT_TOKEN + "*");

        // Get all the notificationList where token contains UPDATED_TOKEN
        defaultNotificationShouldNotBeFound("search=token:*" + UPDATED_TOKEN + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByTokenNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where token does not contain DEFAULT_TOKEN
        defaultNotificationShouldNotBeFound("search=token!*" + DEFAULT_TOKEN + "*");

        // Get all the notificationList where token does not contain UPDATED_TOKEN
        defaultNotificationShouldBeFound("search=token!*" + UPDATED_TOKEN + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject equals to DEFAULT_SUBJECT
        defaultNotificationShouldBeFound("search=subject:" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject equals to UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("search=subject:" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject not equals to DEFAULT_SUBJECT
        defaultNotificationShouldNotBeFound("search=subject!" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject not equals to UPDATED_SUBJECT
        defaultNotificationShouldBeFound("search=subject!" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultNotificationShouldBeFound(String.format("search=( subject:%s OR subject:%s )", DEFAULT_SUBJECT, UPDATED_SUBJECT));

        // Get all the notificationList where subject equals to UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("search=subject:" + UPDATED_SUBJECT);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject is not null
        defaultNotificationShouldBeFound("subject.specified=true");

        // Get all the notificationList where subject is null
        defaultNotificationShouldNotBeFound("subject.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject contains DEFAULT_SUBJECT
        defaultNotificationShouldBeFound("subject.contains=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject contains UPDATED_SUBJECT
        defaultNotificationShouldNotBeFound("subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where subject does not contain DEFAULT_SUBJECT
        defaultNotificationShouldNotBeFound("subject.doesNotContain=" + DEFAULT_SUBJECT);

        // Get all the notificationList where subject does not contain UPDATED_SUBJECT
        defaultNotificationShouldBeFound("subject.doesNotContain=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content equals to DEFAULT_CONTENT
        defaultNotificationShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the notificationList where content equals to UPDATED_CONTENT
        defaultNotificationShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content not equals to DEFAULT_CONTENT
        defaultNotificationShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the notificationList where content not equals to UPDATED_CONTENT
        defaultNotificationShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultNotificationShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the notificationList where content equals to UPDATED_CONTENT
        defaultNotificationShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content is not null
        defaultNotificationShouldBeFound("content.specified=true");

        // Get all the notificationList where content is null
        defaultNotificationShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content contains DEFAULT_CONTENT
        defaultNotificationShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the notificationList where content contains UPDATED_CONTENT
        defaultNotificationShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByContentNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where content does not contain DEFAULT_CONTENT
        defaultNotificationShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the notificationList where content does not contain UPDATED_CONTENT
        defaultNotificationShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image equals to DEFAULT_IMAGE
        defaultNotificationShouldBeFound("image.equals=" + DEFAULT_IMAGE);

        // Get all the notificationList where image equals to UPDATED_IMAGE
        defaultNotificationShouldNotBeFound("image.equals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageIsNotEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image not equals to DEFAULT_IMAGE
        defaultNotificationShouldNotBeFound("image.notEquals=" + DEFAULT_IMAGE);

        // Get all the notificationList where image not equals to UPDATED_IMAGE
        defaultNotificationShouldBeFound("image.notEquals=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageIsInShouldWork() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image in DEFAULT_IMAGE or UPDATED_IMAGE
        defaultNotificationShouldBeFound("image.in=" + DEFAULT_IMAGE + "," + UPDATED_IMAGE);

        // Get all the notificationList where image equals to UPDATED_IMAGE
        defaultNotificationShouldNotBeFound("image.in=" + UPDATED_IMAGE);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageIsNullOrNotNull() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image is not null
        defaultNotificationShouldBeFound("image.specified=true");

        // Get all the notificationList where image is null
        defaultNotificationShouldNotBeFound("image.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image contains DEFAULT_IMAGE
        defaultNotificationShouldBeFound("image.contains=" + DEFAULT_IMAGE);

        // Get all the notificationList where image contains UPDATED_IMAGE
        defaultNotificationShouldNotBeFound("image.contains=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByImageNotContainsSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Get all the notificationList where image does not contain DEFAULT_IMAGE
        defaultNotificationShouldNotBeFound("image.doesNotContain=" + DEFAULT_IMAGE);

        // Get all the notificationList where image does not contain UPDATED_IMAGE
        defaultNotificationShouldBeFound("image.doesNotContain=" + UPDATED_IMAGE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getAllNotificationsByDataIsEqualToSomething() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);
        NotificationData data = NotificationDataResourceIT.createEntity(em);
        em.persist(data);
        em.flush();
        notification.addData(data);
        notificationRepository.saveAndFlush(notification);
        Long dataId = data.getId();

        // Get all the notificationList where data equals to dataId
        defaultNotificationShouldBeFound("dataId.equals=" + dataId);

        // Get all the notificationList where data equals to (dataId + 1)
        defaultNotificationShouldNotBeFound("dataId.equals=" + (dataId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notification.getId().intValue())))
            .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(DEFAULT_IMAGE)));

        // Check, that the count call also returns 1
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultNotificationShouldNotBeFound(String filter) throws Exception {
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restNotificationMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void getNonExistingNotification() throws Exception {
        // Get the notification
        restNotificationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void putNewNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .username(UPDATED_USERNAME)
            .token(UPDATED_TOKEN)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testNotification.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotification.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    void failedPutNewNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).get();
        // Disconnect from session so that the updates on updatedNotification are not directly saved in db
        em.detach(updatedNotification);
        updatedNotification
            .username(UPDATED_USERNAME)
            .token(UPDATED_TOKEN)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void putNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void putWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void putWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testNotification.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotification.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .username(UPDATED_USERNAME)
            .token(UPDATED_TOKEN)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
        Notification testNotification = notificationList.get(notificationList.size() - 1);
        assertThat(testNotification.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testNotification.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testNotification.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testNotification.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testNotification.getImage()).isEqualTo(DEFAULT_IMAGE);
    }

    @Test
    @Transactional
    void failedFullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .username(UPDATED_USERNAME)
            .token(UPDATED_TOKEN)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .image(UPDATED_IMAGE);

        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotification))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void patchNonExistingNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void patchWithIdMismatchNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void patchWithMissingIdPathParamNotification() throws Exception {
        int databaseSizeBeforeUpdate = notificationRepository.findAll().size();
        notification.setId(count.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Notification in the database
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.NOTIFICATION_USER})
    void deleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void failedDeleteNotification() throws Exception {
        // Initialize the database
        notificationRepository.saveAndFlush(notification);

        int databaseSizeBeforeDelete = notificationRepository.findAll().size();

        // Delete the notification
        restNotificationMockMvc
            .perform(delete(ENTITY_API_URL_ID, notification.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        List<Notification> notificationList = notificationRepository.findAll();
        assertThat(notificationList).hasSize(databaseSizeBeforeDelete);
    }
}

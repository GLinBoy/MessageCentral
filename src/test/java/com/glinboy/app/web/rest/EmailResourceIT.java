package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.config.ApplicationProperties;
import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.security.AuthoritiesConstants;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.dto.EmailsDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.mail.Message.RecipientType;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link EmailResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class EmailResourceIT {

    private static final String DEFAULT_RECEIVER = "test@test.com";
    private static final String UPDATED_RECEIVER = "test.update@test.com";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/emails";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_MULTIPLE = "/api/emails/multiple";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmailMockMvc;

    @Autowired
    private ApplicationProperties properties;

    private static final Integer port = 2525;

    private static final String host = "localhost";

    private static String protocol = "smtp";

    private static GreenMail greenMail;

    @BeforeAll
    public static void setupSMTP() {
        greenMail = new GreenMail(new ServerSetup(port, host, protocol));
        greenMail.start();
    }

    @AfterAll
    public static void tearDownSMTP() {
        greenMail.stop();
    }

    @AfterEach
    public void cleanup() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    private Email email;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static Email createEntity(EntityManager em) {
        Email email = new Email().receiver(DEFAULT_RECEIVER).subject(DEFAULT_SUBJECT).content(DEFAULT_CONTENT);
        return email;
    }

    /**
     * Create multiple entities for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static List<EmailsDTO> createEmailsDTO(int emailsDTOCount, int reciversCount) {
        List<EmailsDTO> emailsDTO = IntStream
            .range(0, emailsDTOCount)
            .mapToObj(i -> {
                Set<String> rs = IntStream
                    .range(0, reciversCount)
                    .mapToObj(j -> String.format("test_%d_%d@localhost.com", i, j))
                    .collect(Collectors.toSet());
                EmailsDTO e = new EmailsDTO();
                e.setReceivers(rs);
                e.setSubject(String.format("SUBJECT_%d", i));
                e.setContent(String.format("CONETNT_%d", i));
                return e;
            })
            .collect(Collectors.toList());
        assertThat(emailsDTO.size()).isEqualTo(emailsDTOCount);
        emailsDTO.forEach(es -> assertThat(es.getReceivers().size()).isEqualTo(reciversCount));
        return emailsDTO;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it, if
     * they test an entity which requires the current entity.
     */
    public static Email createUpdatedEntity(EntityManager em) {
        Email email = new Email().receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT).content(UPDATED_CONTENT);
        return email;
    }

    @BeforeEach
    public void initTest() {
        email = createEntity(em);
    }

    @Test
    void checkMailServer() {
        assertThat(EmailResourceIT.greenMail.isRunning()).isEqualTo(Boolean.TRUE);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void createEmail() throws Exception {
        int databaseSizeBeforeCreate = emailRepository.findAll().size();
        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);
        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isCreated());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeCreate + 1);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testEmail.getStatus()).isEqualTo(MessageStatus.IN_QUEUE);

        boolean ok = greenMail.waitForIncomingEmail(10_000, 1);

        if (ok) {
            MimeMessage testMessage = greenMail.getReceivedMessages()[0];
            assertThat(testMessage.getSubject()).isEqualTo(emailDTO.getSubject());
            assertThat(testMessage.getRecipients(RecipientType.TO)[0].toString()).hasToString(emailDTO.getReceiver());
            assertThat(testMessage.getFrom()[0].toString()).hasToString(properties.getEmail().getFrom());

            String emailContent = (String) testMessage.getContent();
            assertThat(emailContent.replaceAll("\\r\\n|\\r|\\n", "")).isEqualTo(emailDTO.getContent());
        } else {
            Assertions.fail("email not sent");
        }
    }

    @Test
    @Transactional
    void failedCreateEmailWithoutRole() throws Exception {
        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);
        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void createBulkEmail() throws Exception {
        int databaseSizeBeforeCreate = emailRepository.findAll().size();
        // Create multiple Email
        int emailsCount = 2;
        int reciverCount = 5;
        List<EmailsDTO> emailsDTO = createEmailsDTO(emailsCount, reciverCount);
        restEmailMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailsDTO))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeCreate + (emailsCount * reciverCount));

        boolean ok = greenMail.waitForIncomingEmail(10_000, 10);

        if (ok) {
            MimeMessage[] testMessages = greenMail.getReceivedMessages();
            assertThat(testMessages).hasSize(emailsCount * reciverCount);
        } else {
            Assertions.fail("email not sent");
        }
    }

    @Test
    @Transactional
    void failedCreateBulkEmail() throws Exception {
        // Create multiple Email
        int emailsCount = 2;
        int reciverCount = 5;
        List<EmailsDTO> emailsDTO = createEmailsDTO(emailsCount, reciverCount);
        restEmailMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailsDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void createEmailWithExistingId() throws Exception {
        // Create the Email with an existing ID
        email.setId(1L);
        EmailDTO emailDTO = emailMapper.toDto(email);

        int databaseSizeBeforeCreate = emailRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void checkReceiverIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailRepository.findAll().size();
        // set the field null
        email.setReceiver(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void checkSubjectIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailRepository.findAll().size();
        // set the field null
        email.setSubject(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmails() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(email.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));
    }

    @Test
    @Transactional
    void failedGetEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList
        restEmailMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc")).andExpect(status().isForbidden());

        // Get the email
        restEmailMockMvc.perform(get(ENTITY_API_URL_ID, email.getId())).andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get the email
        restEmailMockMvc
            .perform(get(ENTITY_API_URL_ID, email.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(email.getId().intValue()))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getEmailsByIdFiltering() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        Long id = email.getId();

        defaultEmailShouldBeFound("query=id==" + id);
        defaultEmailShouldNotBeFound("query=id!=" + id);

        defaultEmailShouldBeFound(String.format("query=id>=%d", id));
        defaultEmailShouldNotBeFound("query=id>" + id);

        defaultEmailShouldBeFound(String.format("query=id<=%d", id));
        defaultEmailShouldNotBeFound("query=id<" + id);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver equals to DEFAULT_RECEIVER
        defaultEmailShouldBeFound("query=receiver==" + DEFAULT_RECEIVER);

        // Get all the emailList where receiver equals to UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("query=receiver==" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverIsNotEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver not equals to DEFAULT_RECEIVER
        defaultEmailShouldNotBeFound("query=receiver!=" + DEFAULT_RECEIVER);

        // Get all the emailList where receiver not equals to UPDATED_RECEIVER
        defaultEmailShouldBeFound("query=receiver!=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverIsInShouldWork() throws Exception {
        // FIXME spring-search:0.2.0 doesn't support in at this moment
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver in DEFAULT_RECEIVER or UPDATED_RECEIVER
        defaultEmailShouldBeFound(String.format("search=( receiver:%s OR receiver:%s )", DEFAULT_RECEIVER, UPDATED_RECEIVER));
        // Get all the emailList where receiver equals to UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("search=receiver:" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver is not null
        defaultEmailShouldBeFound("receiver.specified=true");

        // Get all the emailList where receiver is null
        defaultEmailShouldNotBeFound("receiver.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver contains DEFAULT_RECEIVER
        defaultEmailShouldBeFound("search=receiver:*" + DEFAULT_RECEIVER + "*");

        // Get all the emailList where receiver contains UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("search=receiver:*" + UPDATED_RECEIVER + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsByReceiverNotContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver does not contain DEFAULT_RECEIVER
        defaultEmailShouldNotBeFound("search=receiver!*" + DEFAULT_RECEIVER + "*");

        // Get all the emailList where receiver does not contain UPDATED_RECEIVER
        defaultEmailShouldBeFound("search=receiver!*" + UPDATED_RECEIVER + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject equals to DEFAULT_SUBJECT
        defaultEmailShouldBeFound("query=subject==" + DEFAULT_SUBJECT);

        // Get all the emailList where subject equals to UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("query=subject==" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectIsNotEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject not equals to DEFAULT_SUBJECT
        defaultEmailShouldNotBeFound("search=subject!" + DEFAULT_SUBJECT);

        // Get all the emailList where subject not equals to UPDATED_SUBJECT
        defaultEmailShouldBeFound("search=subject!" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectIsInShouldWork() throws Exception {
        // FIXME spring-search:0.2.0 doesn't support in at this moment
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultEmailShouldBeFound(String.format("search=( subject:%s OR subject:%s )", DEFAULT_SUBJECT, UPDATED_SUBJECT));

        // Get all the emailList where subject equals to UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("search=subject:" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject is not null
        defaultEmailShouldBeFound("subject.specified=true");

        // Get all the emailList where subject is null
        defaultEmailShouldNotBeFound("subject.specified=false");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject contains DEFAULT_SUBJECT
        defaultEmailShouldBeFound("search=subject:*" + DEFAULT_SUBJECT + "*");

        // Get all the emailList where subject contains UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("search=subject:*" + UPDATED_SUBJECT + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getAllEmailsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject does not contain DEFAULT_SUBJECT
        defaultEmailShouldNotBeFound("search=subject!*" + DEFAULT_SUBJECT + "*");

        // Get all the emailList where subject does not contain UPDATED_SUBJECT
        defaultEmailShouldBeFound("search=subject!*" + UPDATED_SUBJECT + "*");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEmailShouldBeFound(String filter) throws Exception {
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(email.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())));

        // Check, that the count call also returns 1
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultEmailShouldNotBeFound(String filter) throws Exception {
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void getNonExistingEmail() throws Exception {
        // Get the email
        restEmailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void putNewEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email
        Email updatedEmail = emailRepository.findById(email.getId()).get();
        // Disconnect from session so that the updates on updatedEmail are not directly
        // saved in db
        em.detach(updatedEmail);
        updatedEmail.receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT).content(UPDATED_CONTENT);
        EmailDTO emailDTO = emailMapper.toDto(updatedEmail);

        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void failedPutNewEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Update the email
        Email updatedEmail = emailRepository.findById(email.getId()).get();
        // Disconnect from session so that the updates on updatedEmail are not directly
        // saved in db
        em.detach(updatedEmail);
        updatedEmail.receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT).content(UPDATED_CONTENT);
        EmailDTO emailDTO = emailMapper.toDto(updatedEmail);

        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void putNonExistingEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void putWithIdMismatchEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void putWithMissingIdPathParamEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void partialUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail.receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmail))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void failedPartialUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail.receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmail))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void fullUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail.receiver(UPDATED_RECEIVER).subject(UPDATED_SUBJECT).content(UPDATED_CONTENT);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmail))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(DEFAULT_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void patchNonExistingEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void patchWithIdMismatchEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void patchWithMissingIdPathParamEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(count.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(emailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = { AuthoritiesConstants.EMAIL_USER })
    void deleteEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeDelete = emailRepository.findAll().size();

        // Delete the email
        restEmailMockMvc
            .perform(delete(ENTITY_API_URL_ID, email.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());

        // Validate the database contains one less item
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void failedDeleteEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeDelete = emailRepository.findAll().size();

        // Delete the email
        restEmailMockMvc
            .perform(delete(ENTITY_API_URL_ID, email.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeDelete);
    }
}

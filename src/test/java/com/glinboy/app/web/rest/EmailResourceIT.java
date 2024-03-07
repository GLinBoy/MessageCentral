package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Email;
import com.glinboy.app.domain.enumeration.EmailType;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.repository.EmailRepository;
import com.glinboy.app.service.dto.EmailDTO;
import com.glinboy.app.service.mapper.EmailMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
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

    private static final String DEFAULT_RECEIVER = "Qad.CmuPsD.Vm+.ZLo@L.4PXW.YQPotM";
    private static final String UPDATED_RECEIVER = "Nb&.OdPePH.*zl.ypDVP.jG.y-mm@rp4nrn.EumiN.vdf.Ea";

    private static final String DEFAULT_SUBJECT = "AAAAAAAAAA";
    private static final String UPDATED_SUBJECT = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final MessageStatus DEFAULT_STATUS = MessageStatus.IN_QUEUE;
    private static final MessageStatus UPDATED_STATUS = MessageStatus.SENT;

    private static final EmailType DEFAULT_EMAIL_TYPE = EmailType.TEXT;
    private static final EmailType UPDATED_EMAIL_TYPE = EmailType.HTML;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/emails";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmailMockMvc;

    private Email email;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Email createEntity(EntityManager em) {
        Email email = new Email()
            .receiver(DEFAULT_RECEIVER)
            .subject(DEFAULT_SUBJECT)
            .content(DEFAULT_CONTENT)
            .status(DEFAULT_STATUS)
            .emailType(DEFAULT_EMAIL_TYPE)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY);
        return email;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Email createUpdatedEntity(EntityManager em) {
        Email email = new Email()
            .receiver(UPDATED_RECEIVER)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .emailType(UPDATED_EMAIL_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
        return email;
    }

    @BeforeEach
    public void initTest() {
        email = createEntity(em);
    }

    @Test
    @Transactional
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
        assertThat(testEmail.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testEmail.getEmailType()).isEqualTo(DEFAULT_EMAIL_TYPE);
        assertThat(testEmail.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testEmail.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
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
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailRepository.findAll().size();
        // set the field null
        email.setCreatedAt(null);

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
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = emailRepository.findAll().size();
        // set the field null
        email.setCreatedBy(null);

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
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].emailType").value(hasItem(DEFAULT_EMAIL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
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
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.emailType").value(DEFAULT_EMAIL_TYPE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getEmailsByIdFiltering() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        Long id = email.getId();

        defaultEmailShouldBeFound("id.equals=" + id);
        defaultEmailShouldNotBeFound("id.notEquals=" + id);

        defaultEmailShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultEmailShouldNotBeFound("id.greaterThan=" + id);

        defaultEmailShouldBeFound("id.lessThanOrEqual=" + id);
        defaultEmailShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver equals to DEFAULT_RECEIVER
        defaultEmailShouldBeFound("receiver.equals=" + DEFAULT_RECEIVER);

        // Get all the emailList where receiver equals to UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("receiver.equals=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver in DEFAULT_RECEIVER or UPDATED_RECEIVER
        defaultEmailShouldBeFound("receiver.in=" + DEFAULT_RECEIVER + "," + UPDATED_RECEIVER);

        // Get all the emailList where receiver equals to UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("receiver.in=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
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
    void getAllEmailsByReceiverContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver contains DEFAULT_RECEIVER
        defaultEmailShouldBeFound("receiver.contains=" + DEFAULT_RECEIVER);

        // Get all the emailList where receiver contains UPDATED_RECEIVER
        defaultEmailShouldNotBeFound("receiver.contains=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverNotContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver does not contain DEFAULT_RECEIVER
        defaultEmailShouldNotBeFound("receiver.doesNotContain=" + DEFAULT_RECEIVER);

        // Get all the emailList where receiver does not contain UPDATED_RECEIVER
        defaultEmailShouldBeFound("receiver.doesNotContain=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject equals to DEFAULT_SUBJECT
        defaultEmailShouldBeFound("subject.equals=" + DEFAULT_SUBJECT);

        // Get all the emailList where subject equals to UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject in DEFAULT_SUBJECT or UPDATED_SUBJECT
        defaultEmailShouldBeFound("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT);

        // Get all the emailList where subject equals to UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
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
    void getAllEmailsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject contains DEFAULT_SUBJECT
        defaultEmailShouldBeFound("subject.contains=" + DEFAULT_SUBJECT);

        // Get all the emailList where subject contains UPDATED_SUBJECT
        defaultEmailShouldNotBeFound("subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where subject does not contain DEFAULT_SUBJECT
        defaultEmailShouldNotBeFound("subject.doesNotContain=" + DEFAULT_SUBJECT);

        // Get all the emailList where subject does not contain UPDATED_SUBJECT
        defaultEmailShouldBeFound("subject.doesNotContain=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where status equals to DEFAULT_STATUS
        defaultEmailShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the emailList where status equals to UPDATED_STATUS
        defaultEmailShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultEmailShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the emailList where status equals to UPDATED_STATUS
        defaultEmailShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where status is not null
        defaultEmailShouldBeFound("status.specified=true");

        // Get all the emailList where status is null
        defaultEmailShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType equals to DEFAULT_EMAIL_TYPE
        defaultEmailShouldBeFound("emailType.equals=" + DEFAULT_EMAIL_TYPE);

        // Get all the emailList where emailType equals to UPDATED_EMAIL_TYPE
        defaultEmailShouldNotBeFound("emailType.equals=" + UPDATED_EMAIL_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType in DEFAULT_EMAIL_TYPE or UPDATED_EMAIL_TYPE
        defaultEmailShouldBeFound("emailType.in=" + DEFAULT_EMAIL_TYPE + "," + UPDATED_EMAIL_TYPE);

        // Get all the emailList where emailType equals to UPDATED_EMAIL_TYPE
        defaultEmailShouldNotBeFound("emailType.in=" + UPDATED_EMAIL_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType is not null
        defaultEmailShouldBeFound("emailType.specified=true");

        // Get all the emailList where emailType is null
        defaultEmailShouldNotBeFound("emailType.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt equals to DEFAULT_CREATED_AT
        defaultEmailShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the emailList where createdAt equals to UPDATED_CREATED_AT
        defaultEmailShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultEmailShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the emailList where createdAt equals to UPDATED_CREATED_AT
        defaultEmailShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt is not null
        defaultEmailShouldBeFound("createdAt.specified=true");

        // Get all the emailList where createdAt is null
        defaultEmailShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy equals to DEFAULT_CREATED_BY
        defaultEmailShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the emailList where createdBy equals to UPDATED_CREATED_BY
        defaultEmailShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultEmailShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the emailList where createdBy equals to UPDATED_CREATED_BY
        defaultEmailShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy is not null
        defaultEmailShouldBeFound("createdBy.specified=true");

        // Get all the emailList where createdBy is null
        defaultEmailShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy contains DEFAULT_CREATED_BY
        defaultEmailShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the emailList where createdBy contains UPDATED_CREATED_BY
        defaultEmailShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy does not contain DEFAULT_CREATED_BY
        defaultEmailShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the emailList where createdBy does not contain UPDATED_CREATED_BY
        defaultEmailShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
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
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].emailType").value(hasItem(DEFAULT_EMAIL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));

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
    void getNonExistingEmail() throws Exception {
        // Get the email
        restEmailMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email
        Email updatedEmail = emailRepository.findById(email.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedEmail are not directly saved in db
        em.detach(updatedEmail);
        updatedEmail
            .receiver(UPDATED_RECEIVER)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .emailType(UPDATED_EMAIL_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
        EmailDTO emailDTO = emailMapper.toDto(updatedEmail);

        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEmail.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testEmail.getEmailType()).isEqualTo(UPDATED_EMAIL_TYPE);
        assertThat(testEmail.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testEmail.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void putNonExistingEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

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
    void partialUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail.subject(UPDATED_SUBJECT).content(UPDATED_CONTENT).status(UPDATED_STATUS);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmail))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(DEFAULT_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEmail.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testEmail.getEmailType()).isEqualTo(DEFAULT_EMAIL_TYPE);
        assertThat(testEmail.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testEmail.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeUpdate = emailRepository.findAll().size();

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail
            .receiver(UPDATED_RECEIVER)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .emailType(UPDATED_EMAIL_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedEmail))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
        Email testEmail = emailList.get(emailList.size() - 1);
        assertThat(testEmail.getReceiver()).isEqualTo(UPDATED_RECEIVER);
        assertThat(testEmail.getSubject()).isEqualTo(UPDATED_SUBJECT);
        assertThat(testEmail.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testEmail.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testEmail.getEmailType()).isEqualTo(UPDATED_EMAIL_TYPE);
        assertThat(testEmail.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testEmail.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmail() throws Exception {
        int databaseSizeBeforeUpdate = emailRepository.findAll().size();
        email.setId(longCount.incrementAndGet());

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
    void deleteEmail() throws Exception {
        // Initialize the database
        emailRepository.saveAndFlush(email);

        int databaseSizeBeforeDelete = emailRepository.findAll().size();

        // Delete the email
        restEmailMockMvc
            .perform(delete(ENTITY_API_URL_ID, email.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Email> emailList = emailRepository.findAll();
        assertThat(emailList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

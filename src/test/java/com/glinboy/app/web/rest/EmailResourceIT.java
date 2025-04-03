package com.glinboy.app.web.rest;

import static com.glinboy.app.domain.EmailAsserts.*;
import static com.glinboy.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
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

    private static final String DEFAULT_RECEIVER = "aLC.Gm@mL4X.ZsUL9.ceF7lx.GhHZ";
    private static final String UPDATED_RECEIVER = "6q.Gn@fKR.-6X.m5iUY6.jqw5.dADhUVR";

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
    private ObjectMapper om;

    @Autowired
    private EmailRepository emailRepository;

    @Autowired
    private EmailMapper emailMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restEmailMockMvc;

    private Email email;

    private Email insertedEmail;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Email createEntity() {
        return new Email()
            .receiver(DEFAULT_RECEIVER)
            .subject(DEFAULT_SUBJECT)
            .content(DEFAULT_CONTENT)
            .status(DEFAULT_STATUS)
            .emailType(DEFAULT_EMAIL_TYPE)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Email createUpdatedEntity() {
        return new Email()
            .receiver(UPDATED_RECEIVER)
            .subject(UPDATED_SUBJECT)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .emailType(UPDATED_EMAIL_TYPE)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
    }

    @BeforeEach
    void initTest() {
        email = createEntity();
    }

    @AfterEach
    void cleanup() {
        if (insertedEmail != null) {
            emailRepository.delete(insertedEmail);
            insertedEmail = null;
        }
    }

    @Test
    @Transactional
    void createEmail() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);
        var returnedEmailDTO = om.readValue(
            restEmailMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            EmailDTO.class
        );

        // Validate the Email in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEmail = emailMapper.toEntity(returnedEmailDTO);
        assertEmailUpdatableFieldsEquals(returnedEmail, getPersistedEmail(returnedEmail));

        insertedEmail = returnedEmail;
    }

    @Test
    @Transactional
    void createEmailWithExistingId() throws Exception {
        // Create the Email with an existing ID
        email.setId(1L);
        EmailDTO emailDTO = emailMapper.toDto(email);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkReceiverIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        email.setReceiver(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubjectIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        email.setSubject(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        email.setCreatedAt(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        email.setCreatedBy(null);

        // Create the Email, which fails.
        EmailDTO emailDTO = emailMapper.toDto(email);

        restEmailMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllEmails() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList
        restEmailMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(email.getId().intValue())))
            .andExpect(jsonPath("$.[*].receiver").value(hasItem(DEFAULT_RECEIVER)))
            .andExpect(jsonPath("$.[*].subject").value(hasItem(DEFAULT_SUBJECT)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].emailType").value(hasItem(DEFAULT_EMAIL_TYPE.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getEmail() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get the email
        restEmailMockMvc
            .perform(get(ENTITY_API_URL_ID, email.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(email.getId().intValue()))
            .andExpect(jsonPath("$.receiver").value(DEFAULT_RECEIVER))
            .andExpect(jsonPath("$.subject").value(DEFAULT_SUBJECT))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.emailType").value(DEFAULT_EMAIL_TYPE.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getEmailsByIdFiltering() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        Long id = email.getId();

        defaultEmailFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEmailFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEmailFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver equals to
        defaultEmailFiltering("receiver.equals=" + DEFAULT_RECEIVER, "receiver.equals=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver in
        defaultEmailFiltering("receiver.in=" + DEFAULT_RECEIVER + "," + UPDATED_RECEIVER, "receiver.in=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver is not null
        defaultEmailFiltering("receiver.specified=true", "receiver.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver contains
        defaultEmailFiltering("receiver.contains=" + DEFAULT_RECEIVER, "receiver.contains=" + UPDATED_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsByReceiverNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where receiver does not contain
        defaultEmailFiltering("receiver.doesNotContain=" + UPDATED_RECEIVER, "receiver.doesNotContain=" + DEFAULT_RECEIVER);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where subject equals to
        defaultEmailFiltering("subject.equals=" + DEFAULT_SUBJECT, "subject.equals=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where subject in
        defaultEmailFiltering("subject.in=" + DEFAULT_SUBJECT + "," + UPDATED_SUBJECT, "subject.in=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where subject is not null
        defaultEmailFiltering("subject.specified=true", "subject.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where subject contains
        defaultEmailFiltering("subject.contains=" + DEFAULT_SUBJECT, "subject.contains=" + UPDATED_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsBySubjectNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where subject does not contain
        defaultEmailFiltering("subject.doesNotContain=" + UPDATED_SUBJECT, "subject.doesNotContain=" + DEFAULT_SUBJECT);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where status equals to
        defaultEmailFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where status in
        defaultEmailFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllEmailsByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where status is not null
        defaultEmailFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType equals to
        defaultEmailFiltering("emailType.equals=" + DEFAULT_EMAIL_TYPE, "emailType.equals=" + UPDATED_EMAIL_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType in
        defaultEmailFiltering("emailType.in=" + DEFAULT_EMAIL_TYPE + "," + UPDATED_EMAIL_TYPE, "emailType.in=" + UPDATED_EMAIL_TYPE);
    }

    @Test
    @Transactional
    void getAllEmailsByEmailTypeIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where emailType is not null
        defaultEmailFiltering("emailType.specified=true", "emailType.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt equals to
        defaultEmailFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt in
        defaultEmailFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdAt is not null
        defaultEmailFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy equals to
        defaultEmailFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy in
        defaultEmailFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy is not null
        defaultEmailFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy contains
        defaultEmailFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllEmailsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        // Get all the emailList where createdBy does not contain
        defaultEmailFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    private void defaultEmailFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultEmailShouldBeFound(shouldBeFound);
        defaultEmailShouldNotBeFound(shouldNotBeFound);
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
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
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
        insertedEmail = emailRepository.saveAndFlush(email);

        long databaseSizeBeforeUpdate = getRepositoryCount();

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
                put(ENTITY_API_URL_ID, emailDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEmailToMatchAllProperties(updatedEmail);
    }

    @Test
    @Transactional
    void putNonExistingEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, emailDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the email using partial update
        Email partialUpdatedEmail = new Email();
        partialUpdatedEmail.setId(email.getId());

        partialUpdatedEmail.receiver(UPDATED_RECEIVER).status(UPDATED_STATUS).createdAt(UPDATED_CREATED_AT);

        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedEmail.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedEmail))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmailUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedEmail, email), getPersistedEmail(email));
    }

    @Test
    @Transactional
    void fullUpdateEmailWithPatch() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        long databaseSizeBeforeUpdate = getRepositoryCount();

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
                    .content(om.writeValueAsBytes(partialUpdatedEmail))
            )
            .andExpect(status().isOk());

        // Validate the Email in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEmailUpdatableFieldsEquals(partialUpdatedEmail, getPersistedEmail(partialUpdatedEmail));
    }

    @Test
    @Transactional
    void patchNonExistingEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, emailDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(emailDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamEmail() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        email.setId(longCount.incrementAndGet());

        // Create the Email
        EmailDTO emailDTO = emailMapper.toDto(email);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restEmailMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(emailDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Email in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteEmail() throws Exception {
        // Initialize the database
        insertedEmail = emailRepository.saveAndFlush(email);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the email
        restEmailMockMvc
            .perform(delete(ENTITY_API_URL_ID, email.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return emailRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected Email getPersistedEmail(Email email) {
        return emailRepository.findById(email.getId()).orElseThrow();
    }

    protected void assertPersistedEmailToMatchAllProperties(Email expectedEmail) {
        assertEmailAllPropertiesEquals(expectedEmail, getPersistedEmail(expectedEmail));
    }

    protected void assertPersistedEmailToMatchUpdatableProperties(Email expectedEmail) {
        assertEmailAllUpdatablePropertiesEquals(expectedEmail, getPersistedEmail(expectedEmail));
    }
}

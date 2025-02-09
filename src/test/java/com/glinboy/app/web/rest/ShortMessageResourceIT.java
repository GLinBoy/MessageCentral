package com.glinboy.app.web.rest;

import static com.glinboy.app.domain.ShortMessageAsserts.*;
import static com.glinboy.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
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
 * Integration tests for the {@link ShortMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShortMessageResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "+40455845";
    private static final String UPDATED_PHONE_NUMBER = "+87●7●9●5●0057";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final MessageStatus DEFAULT_STATUS = MessageStatus.IN_QUEUE;
    private static final MessageStatus UPDATED_STATUS = MessageStatus.SENT;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/short-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ShortMessageRepository shortMessageRepository;

    @Autowired
    private ShortMessageMapper shortMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShortMessageMockMvc;

    private ShortMessage shortMessage;

    private ShortMessage insertedShortMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShortMessage createEntity() {
        return new ShortMessage()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .content(DEFAULT_CONTENT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShortMessage createUpdatedEntity() {
        return new ShortMessage()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
    }

    @BeforeEach
    public void initTest() {
        shortMessage = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedShortMessage != null) {
            shortMessageRepository.delete(insertedShortMessage);
            insertedShortMessage = null;
        }
    }

    @Test
    @Transactional
    void createShortMessage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);
        var returnedShortMessageDTO = om.readValue(
            restShortMessageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            ShortMessageDTO.class
        );

        // Validate the ShortMessage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedShortMessage = shortMessageMapper.toEntity(returnedShortMessageDTO);
        assertShortMessageUpdatableFieldsEquals(returnedShortMessage, getPersistedShortMessage(returnedShortMessage));

        insertedShortMessage = returnedShortMessage;
    }

    @Test
    @Transactional
    void createShortMessageWithExistingId() throws Exception {
        // Create the ShortMessage with an existing ID
        shortMessage.setId(1L);
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShortMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shortMessage.setPhoneNumber(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shortMessage.setContent(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shortMessage.setCreatedAt(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        shortMessage.setCreatedBy(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShortMessages() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shortMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));
    }

    @Test
    @Transactional
    void getShortMessage() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get the shortMessage
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, shortMessage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(shortMessage.getId().intValue()))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY));
    }

    @Test
    @Transactional
    void getShortMessagesByIdFiltering() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        Long id = shortMessage.getId();

        defaultShortMessageFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultShortMessageFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultShortMessageFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber equals to
        defaultShortMessageFiltering("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER, "phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber in
        defaultShortMessageFiltering(
            "phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER,
            "phoneNumber.in=" + UPDATED_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber is not null
        defaultShortMessageFiltering("phoneNumber.specified=true", "phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber contains
        defaultShortMessageFiltering("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER, "phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber does not contain
        defaultShortMessageFiltering(
            "phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER,
            "phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER
        );
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content equals to
        defaultShortMessageFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content in
        defaultShortMessageFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content is not null
        defaultShortMessageFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content contains
        defaultShortMessageFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content does not contain
        defaultShortMessageFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status equals to
        defaultShortMessageFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status in
        defaultShortMessageFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status is not null
        defaultShortMessageFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt equals to
        defaultShortMessageFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt in
        defaultShortMessageFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt is not null
        defaultShortMessageFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy equals to
        defaultShortMessageFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy in
        defaultShortMessageFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy is not null
        defaultShortMessageFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy contains
        defaultShortMessageFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy does not contain
        defaultShortMessageFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    private void defaultShortMessageFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultShortMessageShouldBeFound(shouldBeFound);
        defaultShortMessageShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultShortMessageShouldBeFound(String filter) throws Exception {
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(shortMessage.getId().intValue())))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)));

        // Check, that the count call also returns 1
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultShortMessageShouldNotBeFound(String filter) throws Exception {
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingShortMessage() throws Exception {
        // Get the shortMessage
        restShortMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingShortMessage() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shortMessage
        ShortMessage updatedShortMessage = shortMessageRepository.findById(shortMessage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedShortMessage are not directly saved in db
        em.detach(updatedShortMessage);
        updatedShortMessage
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(updatedShortMessage);

        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shortMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shortMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedShortMessageToMatchAllProperties(updatedShortMessage);
    }

    @Test
    @Transactional
    void putNonExistingShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shortMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShortMessageWithPatch() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shortMessage using partial update
        ShortMessage partialUpdatedShortMessage = new ShortMessage();
        partialUpdatedShortMessage.setId(shortMessage.getId());

        partialUpdatedShortMessage
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .createdBy(UPDATED_CREATED_BY);

        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShortMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShortMessage))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShortMessageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedShortMessage, shortMessage),
            getPersistedShortMessage(shortMessage)
        );
    }

    @Test
    @Transactional
    void fullUpdateShortMessageWithPatch() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the shortMessage using partial update
        ShortMessage partialUpdatedShortMessage = new ShortMessage();
        partialUpdatedShortMessage.setId(shortMessage.getId());

        partialUpdatedShortMessage
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);

        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShortMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedShortMessage))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertShortMessageUpdatableFieldsEquals(partialUpdatedShortMessage, getPersistedShortMessage(partialUpdatedShortMessage));
    }

    @Test
    @Transactional
    void patchNonExistingShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shortMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShortMessage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        shortMessage.setId(longCount.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(shortMessageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShortMessage() throws Exception {
        // Initialize the database
        insertedShortMessage = shortMessageRepository.saveAndFlush(shortMessage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the shortMessage
        restShortMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, shortMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return shortMessageRepository.count();
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

    protected ShortMessage getPersistedShortMessage(ShortMessage shortMessage) {
        return shortMessageRepository.findById(shortMessage.getId()).orElseThrow();
    }

    protected void assertPersistedShortMessageToMatchAllProperties(ShortMessage expectedShortMessage) {
        assertShortMessageAllPropertiesEquals(expectedShortMessage, getPersistedShortMessage(expectedShortMessage));
    }

    protected void assertPersistedShortMessageToMatchUpdatableProperties(ShortMessage expectedShortMessage) {
        assertShortMessageAllUpdatablePropertiesEquals(expectedShortMessage, getPersistedShortMessage(expectedShortMessage));
    }
}

package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.domain.enumeration.MessageStatus;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.criteria.ShortMessageCriteria;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
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

    private static final String DEFAULT_PHONE_NUMBER = "+41●576●08154●5";
    private static final String UPDATED_PHONE_NUMBER = "+173113007●54●2●7";

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
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ShortMessageRepository shortMessageRepository;

    @Autowired
    private ShortMessageMapper shortMessageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restShortMessageMockMvc;

    private ShortMessage shortMessage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShortMessage createEntity(EntityManager em) {
        ShortMessage shortMessage = new ShortMessage()
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .content(DEFAULT_CONTENT)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY);
        return shortMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShortMessage createUpdatedEntity(EntityManager em) {
        ShortMessage shortMessage = new ShortMessage()
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .content(UPDATED_CONTENT)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY);
        return shortMessage;
    }

    @BeforeEach
    public void initTest() {
        shortMessage = createEntity(em);
    }

    @Test
    @Transactional
    void createShortMessage() throws Exception {
        int databaseSizeBeforeCreate = shortMessageRepository.findAll().size();
        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);
        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isCreated());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeCreate + 1);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testShortMessage.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testShortMessage.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testShortMessage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void createShortMessageWithExistingId() throws Exception {
        // Create the ShortMessage with an existing ID
        shortMessage.setId(1L);
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        int databaseSizeBeforeCreate = shortMessageRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = shortMessageRepository.findAll().size();
        // set the field null
        shortMessage.setPhoneNumber(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkContentIsRequired() throws Exception {
        int databaseSizeBeforeTest = shortMessageRepository.findAll().size();
        // set the field null
        shortMessage.setContent(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = shortMessageRepository.findAll().size();
        // set the field null
        shortMessage.setCreatedAt(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = shortMessageRepository.findAll().size();
        // set the field null
        shortMessage.setCreatedBy(null);

        // Create the ShortMessage, which fails.
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllShortMessages() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

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
        shortMessageRepository.saveAndFlush(shortMessage);

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
        shortMessageRepository.saveAndFlush(shortMessage);

        Long id = shortMessage.getId();

        defaultShortMessageShouldBeFound("id.equals=" + id);
        defaultShortMessageShouldNotBeFound("id.notEquals=" + id);

        defaultShortMessageShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultShortMessageShouldNotBeFound("id.greaterThan=" + id);

        defaultShortMessageShouldBeFound("id.lessThanOrEqual=" + id);
        defaultShortMessageShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber is not null
        defaultShortMessageShouldBeFound("phoneNumber.specified=true");

        // Get all the shortMessageList where phoneNumber is null
        defaultShortMessageShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content equals to DEFAULT_CONTENT
        defaultShortMessageShouldBeFound("content.equals=" + DEFAULT_CONTENT);

        // Get all the shortMessageList where content equals to UPDATED_CONTENT
        defaultShortMessageShouldNotBeFound("content.equals=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content in DEFAULT_CONTENT or UPDATED_CONTENT
        defaultShortMessageShouldBeFound("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT);

        // Get all the shortMessageList where content equals to UPDATED_CONTENT
        defaultShortMessageShouldNotBeFound("content.in=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentIsNullOrNotNull() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content is not null
        defaultShortMessageShouldBeFound("content.specified=true");

        // Get all the shortMessageList where content is null
        defaultShortMessageShouldNotBeFound("content.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content contains DEFAULT_CONTENT
        defaultShortMessageShouldBeFound("content.contains=" + DEFAULT_CONTENT);

        // Get all the shortMessageList where content contains UPDATED_CONTENT
        defaultShortMessageShouldNotBeFound("content.contains=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByContentNotContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content does not contain DEFAULT_CONTENT
        defaultShortMessageShouldNotBeFound("content.doesNotContain=" + DEFAULT_CONTENT);

        // Get all the shortMessageList where content does not contain UPDATED_CONTENT
        defaultShortMessageShouldBeFound("content.doesNotContain=" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status equals to DEFAULT_STATUS
        defaultShortMessageShouldBeFound("status.equals=" + DEFAULT_STATUS);

        // Get all the shortMessageList where status equals to UPDATED_STATUS
        defaultShortMessageShouldNotBeFound("status.equals=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status in DEFAULT_STATUS or UPDATED_STATUS
        defaultShortMessageShouldBeFound("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS);

        // Get all the shortMessageList where status equals to UPDATED_STATUS
        defaultShortMessageShouldNotBeFound("status.in=" + UPDATED_STATUS);
    }

    @Test
    @Transactional
    void getAllShortMessagesByStatusIsNullOrNotNull() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where status is not null
        defaultShortMessageShouldBeFound("status.specified=true");

        // Get all the shortMessageList where status is null
        defaultShortMessageShouldNotBeFound("status.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt equals to DEFAULT_CREATED_AT
        defaultShortMessageShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the shortMessageList where createdAt equals to UPDATED_CREATED_AT
        defaultShortMessageShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultShortMessageShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the shortMessageList where createdAt equals to UPDATED_CREATED_AT
        defaultShortMessageShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdAt is not null
        defaultShortMessageShouldBeFound("createdAt.specified=true");

        // Get all the shortMessageList where createdAt is null
        defaultShortMessageShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy equals to DEFAULT_CREATED_BY
        defaultShortMessageShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the shortMessageList where createdBy equals to UPDATED_CREATED_BY
        defaultShortMessageShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultShortMessageShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the shortMessageList where createdBy equals to UPDATED_CREATED_BY
        defaultShortMessageShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy is not null
        defaultShortMessageShouldBeFound("createdBy.specified=true");

        // Get all the shortMessageList where createdBy is null
        defaultShortMessageShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy contains DEFAULT_CREATED_BY
        defaultShortMessageShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the shortMessageList where createdBy contains UPDATED_CREATED_BY
        defaultShortMessageShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllShortMessagesByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where createdBy does not contain DEFAULT_CREATED_BY
        defaultShortMessageShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the shortMessageList where createdBy does not contain UPDATED_CREATED_BY
        defaultShortMessageShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
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
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();

        // Update the shortMessage
        ShortMessage updatedShortMessage = shortMessageRepository.findById(shortMessage.getId()).get();
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
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testShortMessage.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testShortMessage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testShortMessage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void putNonExistingShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, shortMessageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateShortMessageWithPatch() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();

        // Update the shortMessage using partial update
        ShortMessage partialUpdatedShortMessage = new ShortMessage();
        partialUpdatedShortMessage.setId(shortMessage.getId());

        partialUpdatedShortMessage.status(UPDATED_STATUS);

        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShortMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShortMessage))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
        assertThat(testShortMessage.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testShortMessage.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testShortMessage.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void fullUpdateShortMessageWithPatch() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();

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
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShortMessage))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(UPDATED_CONTENT);
        assertThat(testShortMessage.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testShortMessage.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testShortMessage.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void patchNonExistingShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, shortMessageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamShortMessage() throws Exception {
        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();
        shortMessage.setId(count.incrementAndGet());

        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeDelete = shortMessageRepository.findAll().size();

        // Delete the shortMessage
        restShortMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, shortMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

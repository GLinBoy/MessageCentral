package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.service.criteria.ShortMessageCriteria;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;
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
        ShortMessage shortMessage = new ShortMessage().phoneNumber(DEFAULT_PHONE_NUMBER).content(DEFAULT_CONTENT);
        return shortMessage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ShortMessage createUpdatedEntity(EntityManager em) {
        ShortMessage shortMessage = new ShortMessage().phoneNumber(UPDATED_PHONE_NUMBER).content(UPDATED_CONTENT);
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
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));
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
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT));
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
    void getAllShortMessagesByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
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
    void getAllShortMessagesByContentIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content not equals to DEFAULT_CONTENT
        defaultShortMessageShouldNotBeFound("content.notEquals=" + DEFAULT_CONTENT);

        // Get all the shortMessageList where content not equals to UPDATED_CONTENT
        defaultShortMessageShouldBeFound("content.notEquals=" + UPDATED_CONTENT);
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
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)));

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
    void putNewShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeUpdate = shortMessageRepository.findAll().size();

        // Update the shortMessage
        ShortMessage updatedShortMessage = shortMessageRepository.findById(shortMessage.getId()).get();
        // Disconnect from session so that the updates on updatedShortMessage are not directly saved in db
        em.detach(updatedShortMessage);
        updatedShortMessage.phoneNumber(UPDATED_PHONE_NUMBER).content(UPDATED_CONTENT);
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

        partialUpdatedShortMessage.phoneNumber(UPDATED_PHONE_NUMBER).content(UPDATED_CONTENT);

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

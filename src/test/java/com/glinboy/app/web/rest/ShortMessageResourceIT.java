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

import java.util.List;
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
import com.glinboy.app.domain.ShortMessage;
import com.glinboy.app.repository.ShortMessageRepository;
import com.glinboy.app.security.AuthoritiesConstants;
import com.glinboy.app.service.ShortMessageChannelService;
import com.glinboy.app.service.dto.ShortMessageDTO;
import com.glinboy.app.service.dto.ShortMessagesDTO;
import com.glinboy.app.service.mapper.ShortMessageMapper;

/**
 * Integration tests for the {@link ShortMessageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ShortMessageResourceIT {

    private static final String DEFAULT_PHONE_NUMBER = "+12025550179";
    private static final String UPDATED_PHONE_NUMBER = "+989129876543";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/short-messages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_API_URL_MULTIPLE = "/api/short-messages/multiple";

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

    @Mock
    private ShortMessageChannelService<ShortMessageDTO> smsProvider; 

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
	 * Create multiple entity for this test.
	 *
	 * This is a static method, as tests for other entities might also need it, if
	 * they test an entity which requires the current entity.
	 */
	public static List<ShortMessagesDTO> createEmailsDTO(int smsDTOCount, int numbersCount) {
		List<ShortMessagesDTO> shortMessagesDTO = IntStream.range(0, smsDTOCount)
				.mapToObj(i -> {
					Set<String> rs = IntStream.range(0, numbersCount)
						.mapToObj(j -> String.format("+98912989287%d", j))
						.collect(Collectors.toSet());
					ShortMessagesDTO s = new ShortMessagesDTO();
					s.setPhoneNumber(rs);
					s.setContent(String.format("CONETNT_%d", i));
					return s;
				})
				.collect(Collectors.toList());
		assertThat(shortMessagesDTO.size()).isEqualTo(smsDTOCount);
		shortMessagesDTO.forEach(es -> assertThat(es.getPhoneNumbers().size()).isEqualTo(numbersCount));
		return shortMessagesDTO;
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void createShortMessage() throws Exception {
        int databaseSizeBeforeCreate = shortMessageRepository.findAll().size();
        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);
        doNothing().when(smsProvider).sendMessage(shortMessageDTO);
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
    void failedCreateShortMessage() throws Exception {
        // Create the ShortMessage
        ShortMessageDTO shortMessageDTO = shortMessageMapper.toDto(shortMessage);
        doNothing().when(smsProvider).sendMessage(shortMessageDTO);
        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(shortMessageDTO))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void createBulkShortMessage() throws Exception {
        int databaseSizeBeforeCreate = shortMessageRepository.findAll().size();
        // Create Multiple ShortMessage
        int smsCount = 2;
        int numbersCount = 5;
        List<ShortMessagesDTO> shortMessagesDTOs = createEmailsDTO(smsCount, numbersCount);
        doNothing().when(smsProvider).sendMessage(new ShortMessageDTO[0]);
        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(shortMessagesDTOs))
            )
            .andExpect(status().isOk());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeCreate + (smsCount * numbersCount));
    }

    @Test
    @Transactional
    void failedCreateBulkShortMessage() throws Exception {
        // Create Multiple ShortMessage
        int smsCount = 2;
        int numbersCount = 5;
        List<ShortMessagesDTO> shortMessagesDTOs = createEmailsDTO(smsCount, numbersCount);
        doNothing().when(smsProvider).sendMessage(new ShortMessageDTO[0]);
        restShortMessageMockMvc
            .perform(
                post(ENTITY_API_URL_MULTIPLE).contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(shortMessagesDTOs))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    void failedGetShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isForbidden());

        // Get the shortMessage
        restShortMessageMockMvc
            .perform(get(ENTITY_API_URL_ID, shortMessage.getId()))
            .andExpect(status().isForbidden());
    }

    @Disabled(value = "spring-search:0.2.0 doesn't work by *greater than* and *less than* operators at this moment")
	@Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getShortMessagesByIdFiltering() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        Long id = shortMessage.getId();

        defaultShortMessageShouldBeFound("search=id:" + id);
        defaultShortMessageShouldNotBeFound("search=id!" + id);

        defaultShortMessageShouldBeFound(String.format("search=( id:%d OR id>%d )", id, id));
        defaultShortMessageShouldNotBeFound("search=id>" + id);

        defaultShortMessageShouldBeFound(String.format("search=( id:%d OR id<%d )", id, id));
        defaultShortMessageShouldNotBeFound("search=id<" + id);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getAllShortMessagesByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldBeFound("search=phoneNumber:" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("search=phoneNumber:" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getAllShortMessagesByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("search=phoneNumber!" + DEFAULT_PHONE_NUMBER);

        // Get all the shortMessageList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound("search=phoneNumber!" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getAllShortMessagesByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound(String.format("search=( phoneNumber:%s OR phoneNumber:%s )", DEFAULT_PHONE_NUMBER, UPDATED_PHONE_NUMBER));

        // Get all the shortMessageList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("search=phoneNumber:" + UPDATED_PHONE_NUMBER);
    }

    @Disabled(value = "spring-search:0.2.0 doesn't support *specified* at this moment")
    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getAllShortMessagesByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultShortMessageShouldNotBeFound("search=phoneNumber!*" + DEFAULT_PHONE_NUMBER + "*");

        // Get all the shortMessageList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultShortMessageShouldBeFound("search=phoneNumber!*" + UPDATED_PHONE_NUMBER + "*");
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getAllShortMessagesByContentIsEqualToSomething() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Get all the shortMessageList where content equals to DEFAULT_CONTENT
        defaultShortMessageShouldBeFound("search=content:" + DEFAULT_CONTENT);

        // Get all the shortMessageList where content equals to UPDATED_CONTENT
        defaultShortMessageShouldNotBeFound("search=content:" + UPDATED_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void getNonExistingShortMessage() throws Exception {
        // Get the shortMessage
        restShortMessageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void failedPutNewShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

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
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    void failedPartialUpdateShortMessageWithPatch() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        // Update the shortMessage using partial update
        ShortMessage partialUpdatedShortMessage = new ShortMessage();
        partialUpdatedShortMessage.setId(shortMessage.getId());

        restShortMessageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedShortMessage.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedShortMessage))
            )
            .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
        ShortMessage testShortMessage = shortMessageList.get(shortMessageList.size() - 1);
        assertThat(testShortMessage.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testShortMessage.getContent()).isEqualTo(DEFAULT_CONTENT);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
            .andExpect(status().isMethodNotAllowed());

        // Validate the ShortMessage in the database
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
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
    @WithMockUser(authorities = {AuthoritiesConstants.SMS_USER})
    void deleteShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeDelete = shortMessageRepository.findAll().size();

        // Delete the shortMessage
        restShortMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, shortMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isMethodNotAllowed());

        // Validate the database contains one less item
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeDelete);
    }

    @Test
    @Transactional
    void failedDeleteShortMessage() throws Exception {
        // Initialize the database
        shortMessageRepository.saveAndFlush(shortMessage);

        int databaseSizeBeforeDelete = shortMessageRepository.findAll().size();

        // Delete the shortMessage
        restShortMessageMockMvc
            .perform(delete(ENTITY_API_URL_ID, shortMessage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());

        // Validate the database contains one less item
        List<ShortMessage> shortMessageList = shortMessageRepository.findAll();
        assertThat(shortMessageList).hasSize(databaseSizeBeforeDelete);
    }
}

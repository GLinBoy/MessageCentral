package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.criteria.TokenCriteria;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
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
 * Integration tests for the {@link TokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class TokenResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DISABLE = false;
    private static final Boolean UPDATED_DISABLE = true;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_DEPRECATE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPRECATE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_ROLES = 1;
    private static final Integer UPDATED_ROLES = 2;
    private static final Integer SMALLER_ROLES = 1 - 1;

    private static final String ENTITY_API_URL = "/api/tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTokenMockMvc;

    private Token token;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Token createEntity(EntityManager em) {
        Token token = new Token()
            .name(DEFAULT_NAME)
            .token(DEFAULT_TOKEN)
            .disable(DEFAULT_DISABLE)
            .createdAt(DEFAULT_CREATED_AT)
            .deprecateAt(DEFAULT_DEPRECATE_AT)
            .roles(DEFAULT_ROLES);
        return token;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Token createUpdatedEntity(EntityManager em) {
        Token token = new Token()
            .name(UPDATED_NAME)
            .token(UPDATED_TOKEN)
            .disable(UPDATED_DISABLE)
            .createdAt(UPDATED_CREATED_AT)
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES);
        return token;
    }

    @BeforeEach
    public void initTest() {
        token = createEntity(em);
    }

    @Test
    @Transactional
    void createToken() throws Exception {
        int databaseSizeBeforeCreate = tokenRepository.findAll().size();
        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);
        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isCreated());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeCreate + 1);
        Token testToken = tokenList.get(tokenList.size() - 1);
        assertThat(testToken.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testToken.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testToken.getDisable()).isEqualTo(DEFAULT_DISABLE);
        assertThat(testToken.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testToken.getDeprecateAt()).isEqualTo(DEFAULT_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(DEFAULT_ROLES);
    }

    @Test
    @Transactional
    void createTokenWithExistingId() throws Exception {
        // Create the Token with an existing ID
        token.setId(1L);
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        int databaseSizeBeforeCreate = tokenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setName(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setToken(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisableIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setDisable(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setCreatedAt(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeprecateAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setDeprecateAt(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRolesIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setRoles(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTokens() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList
        restTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(token.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].disable").value(hasItem(DEFAULT_DISABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].deprecateAt").value(hasItem(DEFAULT_DEPRECATE_AT.toString())))
            .andExpect(jsonPath("$.[*].roles").value(hasItem(DEFAULT_ROLES)));
    }

    @Test
    @Transactional
    void getToken() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get the token
        restTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, token.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(token.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.disable").value(DEFAULT_DISABLE.booleanValue()))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.deprecateAt").value(DEFAULT_DEPRECATE_AT.toString()))
            .andExpect(jsonPath("$.roles").value(DEFAULT_ROLES));
    }

    @Test
    @Transactional
    void getTokensByIdFiltering() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        Long id = token.getId();

        defaultTokenShouldBeFound("id.equals=" + id);
        defaultTokenShouldNotBeFound("id.notEquals=" + id);

        defaultTokenShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultTokenShouldNotBeFound("id.greaterThan=" + id);

        defaultTokenShouldBeFound("id.lessThanOrEqual=" + id);
        defaultTokenShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name equals to DEFAULT_NAME
        defaultTokenShouldBeFound("name.equals=" + DEFAULT_NAME);

        // Get all the tokenList where name equals to UPDATED_NAME
        defaultTokenShouldNotBeFound("name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name not equals to DEFAULT_NAME
        defaultTokenShouldNotBeFound("name.notEquals=" + DEFAULT_NAME);

        // Get all the tokenList where name not equals to UPDATED_NAME
        defaultTokenShouldBeFound("name.notEquals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name in DEFAULT_NAME or UPDATED_NAME
        defaultTokenShouldBeFound("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME);

        // Get all the tokenList where name equals to UPDATED_NAME
        defaultTokenShouldNotBeFound("name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name is not null
        defaultTokenShouldBeFound("name.specified=true");

        // Get all the tokenList where name is null
        defaultTokenShouldNotBeFound("name.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByNameContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name contains DEFAULT_NAME
        defaultTokenShouldBeFound("name.contains=" + DEFAULT_NAME);

        // Get all the tokenList where name contains UPDATED_NAME
        defaultTokenShouldNotBeFound("name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameNotContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name does not contain DEFAULT_NAME
        defaultTokenShouldNotBeFound("name.doesNotContain=" + DEFAULT_NAME);

        // Get all the tokenList where name does not contain UPDATED_NAME
        defaultTokenShouldBeFound("name.doesNotContain=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token equals to DEFAULT_TOKEN
        defaultTokenShouldBeFound("token.equals=" + DEFAULT_TOKEN);

        // Get all the tokenList where token equals to UPDATED_TOKEN
        defaultTokenShouldNotBeFound("token.equals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token not equals to DEFAULT_TOKEN
        defaultTokenShouldNotBeFound("token.notEquals=" + DEFAULT_TOKEN);

        // Get all the tokenList where token not equals to UPDATED_TOKEN
        defaultTokenShouldBeFound("token.notEquals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token in DEFAULT_TOKEN or UPDATED_TOKEN
        defaultTokenShouldBeFound("token.in=" + DEFAULT_TOKEN + "," + UPDATED_TOKEN);

        // Get all the tokenList where token equals to UPDATED_TOKEN
        defaultTokenShouldNotBeFound("token.in=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token is not null
        defaultTokenShouldBeFound("token.specified=true");

        // Get all the tokenList where token is null
        defaultTokenShouldNotBeFound("token.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByTokenContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token contains DEFAULT_TOKEN
        defaultTokenShouldBeFound("token.contains=" + DEFAULT_TOKEN);

        // Get all the tokenList where token contains UPDATED_TOKEN
        defaultTokenShouldNotBeFound("token.contains=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenNotContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token does not contain DEFAULT_TOKEN
        defaultTokenShouldNotBeFound("token.doesNotContain=" + DEFAULT_TOKEN);

        // Get all the tokenList where token does not contain UPDATED_TOKEN
        defaultTokenShouldBeFound("token.doesNotContain=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable equals to DEFAULT_DISABLE
        defaultTokenShouldBeFound("disable.equals=" + DEFAULT_DISABLE);

        // Get all the tokenList where disable equals to UPDATED_DISABLE
        defaultTokenShouldNotBeFound("disable.equals=" + UPDATED_DISABLE);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable not equals to DEFAULT_DISABLE
        defaultTokenShouldNotBeFound("disable.notEquals=" + DEFAULT_DISABLE);

        // Get all the tokenList where disable not equals to UPDATED_DISABLE
        defaultTokenShouldBeFound("disable.notEquals=" + UPDATED_DISABLE);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable in DEFAULT_DISABLE or UPDATED_DISABLE
        defaultTokenShouldBeFound("disable.in=" + DEFAULT_DISABLE + "," + UPDATED_DISABLE);

        // Get all the tokenList where disable equals to UPDATED_DISABLE
        defaultTokenShouldNotBeFound("disable.in=" + UPDATED_DISABLE);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable is not null
        defaultTokenShouldBeFound("disable.specified=true");

        // Get all the tokenList where disable is null
        defaultTokenShouldNotBeFound("disable.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt equals to DEFAULT_CREATED_AT
        defaultTokenShouldBeFound("createdAt.equals=" + DEFAULT_CREATED_AT);

        // Get all the tokenList where createdAt equals to UPDATED_CREATED_AT
        defaultTokenShouldNotBeFound("createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt not equals to DEFAULT_CREATED_AT
        defaultTokenShouldNotBeFound("createdAt.notEquals=" + DEFAULT_CREATED_AT);

        // Get all the tokenList where createdAt not equals to UPDATED_CREATED_AT
        defaultTokenShouldBeFound("createdAt.notEquals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt in DEFAULT_CREATED_AT or UPDATED_CREATED_AT
        defaultTokenShouldBeFound("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT);

        // Get all the tokenList where createdAt equals to UPDATED_CREATED_AT
        defaultTokenShouldNotBeFound("createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt is not null
        defaultTokenShouldBeFound("createdAt.specified=true");

        // Get all the tokenList where createdAt is null
        defaultTokenShouldNotBeFound("createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt equals to DEFAULT_DEPRECATE_AT
        defaultTokenShouldBeFound("deprecateAt.equals=" + DEFAULT_DEPRECATE_AT);

        // Get all the tokenList where deprecateAt equals to UPDATED_DEPRECATE_AT
        defaultTokenShouldNotBeFound("deprecateAt.equals=" + UPDATED_DEPRECATE_AT);
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt not equals to DEFAULT_DEPRECATE_AT
        defaultTokenShouldNotBeFound("deprecateAt.notEquals=" + DEFAULT_DEPRECATE_AT);

        // Get all the tokenList where deprecateAt not equals to UPDATED_DEPRECATE_AT
        defaultTokenShouldBeFound("deprecateAt.notEquals=" + UPDATED_DEPRECATE_AT);
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt in DEFAULT_DEPRECATE_AT or UPDATED_DEPRECATE_AT
        defaultTokenShouldBeFound("deprecateAt.in=" + DEFAULT_DEPRECATE_AT + "," + UPDATED_DEPRECATE_AT);

        // Get all the tokenList where deprecateAt equals to UPDATED_DEPRECATE_AT
        defaultTokenShouldNotBeFound("deprecateAt.in=" + UPDATED_DEPRECATE_AT);
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt is not null
        defaultTokenShouldBeFound("deprecateAt.specified=true");

        // Get all the tokenList where deprecateAt is null
        defaultTokenShouldNotBeFound("deprecateAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles equals to DEFAULT_ROLES
        defaultTokenShouldBeFound("roles.equals=" + DEFAULT_ROLES);

        // Get all the tokenList where roles equals to UPDATED_ROLES
        defaultTokenShouldNotBeFound("roles.equals=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsNotEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles not equals to DEFAULT_ROLES
        defaultTokenShouldNotBeFound("roles.notEquals=" + DEFAULT_ROLES);

        // Get all the tokenList where roles not equals to UPDATED_ROLES
        defaultTokenShouldBeFound("roles.notEquals=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles in DEFAULT_ROLES or UPDATED_ROLES
        defaultTokenShouldBeFound("roles.in=" + DEFAULT_ROLES + "," + UPDATED_ROLES);

        // Get all the tokenList where roles equals to UPDATED_ROLES
        defaultTokenShouldNotBeFound("roles.in=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is not null
        defaultTokenShouldBeFound("roles.specified=true");

        // Get all the tokenList where roles is null
        defaultTokenShouldNotBeFound("roles.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is greater than or equal to DEFAULT_ROLES
        defaultTokenShouldBeFound("roles.greaterThanOrEqual=" + DEFAULT_ROLES);

        // Get all the tokenList where roles is greater than or equal to UPDATED_ROLES
        defaultTokenShouldNotBeFound("roles.greaterThanOrEqual=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is less than or equal to DEFAULT_ROLES
        defaultTokenShouldBeFound("roles.lessThanOrEqual=" + DEFAULT_ROLES);

        // Get all the tokenList where roles is less than or equal to SMALLER_ROLES
        defaultTokenShouldNotBeFound("roles.lessThanOrEqual=" + SMALLER_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsLessThanSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is less than DEFAULT_ROLES
        defaultTokenShouldNotBeFound("roles.lessThan=" + DEFAULT_ROLES);

        // Get all the tokenList where roles is less than UPDATED_ROLES
        defaultTokenShouldBeFound("roles.lessThan=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is greater than DEFAULT_ROLES
        defaultTokenShouldNotBeFound("roles.greaterThan=" + DEFAULT_ROLES);

        // Get all the tokenList where roles is greater than SMALLER_ROLES
        defaultTokenShouldBeFound("roles.greaterThan=" + SMALLER_ROLES);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTokenShouldBeFound(String filter) throws Exception {
        restTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(token.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].disable").value(hasItem(DEFAULT_DISABLE.booleanValue())))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].deprecateAt").value(hasItem(DEFAULT_DEPRECATE_AT.toString())))
            .andExpect(jsonPath("$.[*].roles").value(hasItem(DEFAULT_ROLES)));

        // Check, that the count call also returns 1
        restTokenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultTokenShouldNotBeFound(String filter) throws Exception {
        restTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restTokenMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingToken() throws Exception {
        // Get the token
        restTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewToken() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();

        // Update the token
        Token updatedToken = tokenRepository.findById(token.getId()).get();
        // Disconnect from session so that the updates on updatedToken are not directly saved in db
        em.detach(updatedToken);
        updatedToken
            .name(UPDATED_NAME)
            .token(UPDATED_TOKEN)
            .disable(UPDATED_DISABLE)
            .createdAt(UPDATED_CREATED_AT)
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES);
        TokenDTO tokenDTO = tokenMapper.toDto(updatedToken);

        restTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
        Token testToken = tokenList.get(tokenList.size() - 1);
        assertThat(testToken.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testToken.getDisable()).isEqualTo(UPDATED_DISABLE);
        assertThat(testToken.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testToken.getDeprecateAt()).isEqualTo(UPDATED_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(UPDATED_ROLES);
    }

    @Test
    @Transactional
    void putNonExistingToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTokenWithPatch() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();

        // Update the token using partial update
        Token partialUpdatedToken = new Token();
        partialUpdatedToken.setId(token.getId());

        partialUpdatedToken.name(UPDATED_NAME).roles(UPDATED_ROLES);

        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedToken))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
        Token testToken = tokenList.get(tokenList.size() - 1);
        assertThat(testToken.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testToken.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testToken.getDisable()).isEqualTo(DEFAULT_DISABLE);
        assertThat(testToken.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testToken.getDeprecateAt()).isEqualTo(DEFAULT_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(UPDATED_ROLES);
    }

    @Test
    @Transactional
    void fullUpdateTokenWithPatch() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();

        // Update the token using partial update
        Token partialUpdatedToken = new Token();
        partialUpdatedToken.setId(token.getId());

        partialUpdatedToken
            .name(UPDATED_NAME)
            .token(UPDATED_TOKEN)
            .disable(UPDATED_DISABLE)
            .createdAt(UPDATED_CREATED_AT)
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES);

        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedToken))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
        Token testToken = tokenList.get(tokenList.size() - 1);
        assertThat(testToken.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testToken.getDisable()).isEqualTo(UPDATED_DISABLE);
        assertThat(testToken.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testToken.getDeprecateAt()).isEqualTo(UPDATED_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(UPDATED_ROLES);
    }

    @Test
    @Transactional
    void patchNonExistingToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamToken() throws Exception {
        int databaseSizeBeforeUpdate = tokenRepository.findAll().size();
        token.setId(count.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(tokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Token in the database
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteToken() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        int databaseSizeBeforeDelete = tokenRepository.findAll().size();

        // Delete the token
        restTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, token.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Token> tokenList = tokenRepository.findAll();
        assertThat(tokenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
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

    private static final Instant DEFAULT_DEPRECATE_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_DEPRECATE_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Integer DEFAULT_ROLES = 1;
    private static final Integer UPDATED_ROLES = 2;
    private static final Integer SMALLER_ROLES = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_UPDATED_BY = "BBBBBBBBBB";

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
            .deprecateAt(DEFAULT_DEPRECATE_AT)
            .roles(DEFAULT_ROLES)
            .createdAt(DEFAULT_CREATED_AT)
            .createdBy(DEFAULT_CREATED_BY)
            .updatedAt(DEFAULT_UPDATED_AT)
            .updatedBy(DEFAULT_UPDATED_BY);
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
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
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
        assertThat(testToken.getDeprecateAt()).isEqualTo(DEFAULT_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(DEFAULT_ROLES);
        assertThat(testToken.getCreatedAt()).isEqualTo(DEFAULT_CREATED_AT);
        assertThat(testToken.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testToken.getUpdatedAt()).isEqualTo(DEFAULT_UPDATED_AT);
        assertThat(testToken.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
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
    void checkCreatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setCreatedBy(null);

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
    void checkUpdatedAtIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setUpdatedAt(null);

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
    void checkUpdatedByIsRequired() throws Exception {
        int databaseSizeBeforeTest = tokenRepository.findAll().size();
        // set the field null
        token.setUpdatedBy(null);

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
            .andExpect(jsonPath("$.[*].deprecateAt").value(hasItem(DEFAULT_DEPRECATE_AT.toString())))
            .andExpect(jsonPath("$.[*].roles").value(hasItem(DEFAULT_ROLES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)));
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
            .andExpect(jsonPath("$.deprecateAt").value(DEFAULT_DEPRECATE_AT.toString()))
            .andExpect(jsonPath("$.roles").value(DEFAULT_ROLES))
            .andExpect(jsonPath("$.createdAt").value(DEFAULT_CREATED_AT.toString()))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.updatedAt").value(DEFAULT_UPDATED_AT.toString()))
            .andExpect(jsonPath("$.updatedBy").value(DEFAULT_UPDATED_BY));
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
    void getAllTokensByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy equals to DEFAULT_CREATED_BY
        defaultTokenShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the tokenList where createdBy equals to UPDATED_CREATED_BY
        defaultTokenShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultTokenShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the tokenList where createdBy equals to UPDATED_CREATED_BY
        defaultTokenShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy is not null
        defaultTokenShouldBeFound("createdBy.specified=true");

        // Get all the tokenList where createdBy is null
        defaultTokenShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy contains DEFAULT_CREATED_BY
        defaultTokenShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the tokenList where createdBy contains UPDATED_CREATED_BY
        defaultTokenShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy does not contain DEFAULT_CREATED_BY
        defaultTokenShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the tokenList where createdBy does not contain UPDATED_CREATED_BY
        defaultTokenShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt equals to DEFAULT_UPDATED_AT
        defaultTokenShouldBeFound("updatedAt.equals=" + DEFAULT_UPDATED_AT);

        // Get all the tokenList where updatedAt equals to UPDATED_UPDATED_AT
        defaultTokenShouldNotBeFound("updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt in DEFAULT_UPDATED_AT or UPDATED_UPDATED_AT
        defaultTokenShouldBeFound("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT);

        // Get all the tokenList where updatedAt equals to UPDATED_UPDATED_AT
        defaultTokenShouldNotBeFound("updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt is not null
        defaultTokenShouldBeFound("updatedAt.specified=true");

        // Get all the tokenList where updatedAt is null
        defaultTokenShouldNotBeFound("updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy equals to DEFAULT_UPDATED_BY
        defaultTokenShouldBeFound("updatedBy.equals=" + DEFAULT_UPDATED_BY);

        // Get all the tokenList where updatedBy equals to UPDATED_UPDATED_BY
        defaultTokenShouldNotBeFound("updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy in DEFAULT_UPDATED_BY or UPDATED_UPDATED_BY
        defaultTokenShouldBeFound("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY);

        // Get all the tokenList where updatedBy equals to UPDATED_UPDATED_BY
        defaultTokenShouldNotBeFound("updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy is not null
        defaultTokenShouldBeFound("updatedBy.specified=true");

        // Get all the tokenList where updatedBy is null
        defaultTokenShouldNotBeFound("updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy contains DEFAULT_UPDATED_BY
        defaultTokenShouldBeFound("updatedBy.contains=" + DEFAULT_UPDATED_BY);

        // Get all the tokenList where updatedBy contains UPDATED_UPDATED_BY
        defaultTokenShouldNotBeFound("updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy does not contain DEFAULT_UPDATED_BY
        defaultTokenShouldNotBeFound("updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);

        // Get all the tokenList where updatedBy does not contain UPDATED_UPDATED_BY
        defaultTokenShouldBeFound("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY);
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
            .andExpect(jsonPath("$.[*].deprecateAt").value(hasItem(DEFAULT_DEPRECATE_AT.toString())))
            .andExpect(jsonPath("$.[*].roles").value(hasItem(DEFAULT_ROLES)))
            .andExpect(jsonPath("$.[*].createdAt").value(hasItem(DEFAULT_CREATED_AT.toString())))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].updatedAt").value(hasItem(DEFAULT_UPDATED_AT.toString())))
            .andExpect(jsonPath("$.[*].updatedBy").value(hasItem(DEFAULT_UPDATED_BY)));

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
    void putExistingToken() throws Exception {
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
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);
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
        assertThat(testToken.getDeprecateAt()).isEqualTo(UPDATED_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(UPDATED_ROLES);
        assertThat(testToken.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testToken.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testToken.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testToken.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
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

        partialUpdatedToken.name(UPDATED_NAME).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT);

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
        assertThat(testToken.getDeprecateAt()).isEqualTo(DEFAULT_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(DEFAULT_ROLES);
        assertThat(testToken.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testToken.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testToken.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testToken.getUpdatedBy()).isEqualTo(DEFAULT_UPDATED_BY);
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
            .deprecateAt(UPDATED_DEPRECATE_AT)
            .roles(UPDATED_ROLES)
            .createdAt(UPDATED_CREATED_AT)
            .createdBy(UPDATED_CREATED_BY)
            .updatedAt(UPDATED_UPDATED_AT)
            .updatedBy(UPDATED_UPDATED_BY);

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
        assertThat(testToken.getDeprecateAt()).isEqualTo(UPDATED_DEPRECATE_AT);
        assertThat(testToken.getRoles()).isEqualTo(UPDATED_ROLES);
        assertThat(testToken.getCreatedAt()).isEqualTo(UPDATED_CREATED_AT);
        assertThat(testToken.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testToken.getUpdatedAt()).isEqualTo(UPDATED_UPDATED_AT);
        assertThat(testToken.getUpdatedBy()).isEqualTo(UPDATED_UPDATED_BY);
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

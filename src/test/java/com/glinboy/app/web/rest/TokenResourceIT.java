package com.glinboy.app.web.rest;

import static com.glinboy.app.domain.TokenAsserts.*;
import static com.glinboy.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Token;
import com.glinboy.app.repository.TokenRepository;
import com.glinboy.app.service.dto.TokenDTO;
import com.glinboy.app.service.mapper.TokenMapper;
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
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenMapper tokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTokenMockMvc;

    private Token token;

    private Token insertedToken;

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

    @AfterEach
    public void cleanup() {
        if (insertedToken != null) {
            tokenRepository.delete(insertedToken);
            insertedToken = null;
        }
    }

    @Test
    @Transactional
    void createToken() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);
        var returnedTokenDTO = om.readValue(
            restTokenMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            TokenDTO.class
        );

        // Validate the Token in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedToken = tokenMapper.toEntity(returnedTokenDTO);
        assertTokenUpdatableFieldsEquals(returnedToken, getPersistedToken(returnedToken));

        insertedToken = returnedToken;
    }

    @Test
    @Transactional
    void createTokenWithExistingId() throws Exception {
        // Create the Token with an existing ID
        token.setId(1L);
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setName(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setToken(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDisableIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setDisable(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDeprecateAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setDeprecateAt(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkRolesIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setRoles(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setCreatedAt(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCreatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setCreatedBy(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setUpdatedAt(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUpdatedByIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        token.setUpdatedBy(null);

        // Create the Token, which fails.
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        restTokenMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllTokens() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

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
        insertedToken = tokenRepository.saveAndFlush(token);

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
        insertedToken = tokenRepository.saveAndFlush(token);

        Long id = token.getId();

        defaultTokenFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTokenFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTokenFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name equals to
        defaultTokenFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name in
        defaultTokenFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name is not null
        defaultTokenFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByNameContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name contains
        defaultTokenFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByNameNotContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where name does not contain
        defaultTokenFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token equals to
        defaultTokenFiltering("token.equals=" + DEFAULT_TOKEN, "token.equals=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token in
        defaultTokenFiltering("token.in=" + DEFAULT_TOKEN + "," + UPDATED_TOKEN, "token.in=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token is not null
        defaultTokenFiltering("token.specified=true", "token.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByTokenContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token contains
        defaultTokenFiltering("token.contains=" + DEFAULT_TOKEN, "token.contains=" + UPDATED_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByTokenNotContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where token does not contain
        defaultTokenFiltering("token.doesNotContain=" + UPDATED_TOKEN, "token.doesNotContain=" + DEFAULT_TOKEN);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable equals to
        defaultTokenFiltering("disable.equals=" + DEFAULT_DISABLE, "disable.equals=" + UPDATED_DISABLE);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable in
        defaultTokenFiltering("disable.in=" + DEFAULT_DISABLE + "," + UPDATED_DISABLE, "disable.in=" + UPDATED_DISABLE);
    }

    @Test
    @Transactional
    void getAllTokensByDisableIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where disable is not null
        defaultTokenFiltering("disable.specified=true", "disable.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt equals to
        defaultTokenFiltering("deprecateAt.equals=" + DEFAULT_DEPRECATE_AT, "deprecateAt.equals=" + UPDATED_DEPRECATE_AT);
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt in
        defaultTokenFiltering(
            "deprecateAt.in=" + DEFAULT_DEPRECATE_AT + "," + UPDATED_DEPRECATE_AT,
            "deprecateAt.in=" + UPDATED_DEPRECATE_AT
        );
    }

    @Test
    @Transactional
    void getAllTokensByDeprecateAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where deprecateAt is not null
        defaultTokenFiltering("deprecateAt.specified=true", "deprecateAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles equals to
        defaultTokenFiltering("roles.equals=" + DEFAULT_ROLES, "roles.equals=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles in
        defaultTokenFiltering("roles.in=" + DEFAULT_ROLES + "," + UPDATED_ROLES, "roles.in=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is not null
        defaultTokenFiltering("roles.specified=true", "roles.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is greater than or equal to
        defaultTokenFiltering("roles.greaterThanOrEqual=" + DEFAULT_ROLES, "roles.greaterThanOrEqual=" + UPDATED_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is less than or equal to
        defaultTokenFiltering("roles.lessThanOrEqual=" + DEFAULT_ROLES, "roles.lessThanOrEqual=" + SMALLER_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsLessThanSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is less than
        defaultTokenFiltering("roles.lessThan=" + UPDATED_ROLES, "roles.lessThan=" + DEFAULT_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByRolesIsGreaterThanSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where roles is greater than
        defaultTokenFiltering("roles.greaterThan=" + SMALLER_ROLES, "roles.greaterThan=" + DEFAULT_ROLES);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt equals to
        defaultTokenFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt in
        defaultTokenFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdAt is not null
        defaultTokenFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy equals to
        defaultTokenFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy in
        defaultTokenFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy is not null
        defaultTokenFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy contains
        defaultTokenFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where createdBy does not contain
        defaultTokenFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt equals to
        defaultTokenFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt in
        defaultTokenFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedAtIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedAt is not null
        defaultTokenFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy equals to
        defaultTokenFiltering("updatedBy.equals=" + DEFAULT_UPDATED_BY, "updatedBy.equals=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsInShouldWork() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy in
        defaultTokenFiltering("updatedBy.in=" + DEFAULT_UPDATED_BY + "," + UPDATED_UPDATED_BY, "updatedBy.in=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy is not null
        defaultTokenFiltering("updatedBy.specified=true", "updatedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy contains
        defaultTokenFiltering("updatedBy.contains=" + DEFAULT_UPDATED_BY, "updatedBy.contains=" + UPDATED_UPDATED_BY);
    }

    @Test
    @Transactional
    void getAllTokensByUpdatedByNotContainsSomething() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        // Get all the tokenList where updatedBy does not contain
        defaultTokenFiltering("updatedBy.doesNotContain=" + UPDATED_UPDATED_BY, "updatedBy.doesNotContain=" + DEFAULT_UPDATED_BY);
    }

    private void defaultTokenFiltering(String shouldBeFound, String shouldNotBeFound) throws Exception {
        defaultTokenShouldBeFound(shouldBeFound);
        defaultTokenShouldNotBeFound(shouldNotBeFound);
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
        insertedToken = tokenRepository.saveAndFlush(token);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the token
        Token updatedToken = tokenRepository.findById(token.getId()).orElseThrow();
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
                put(ENTITY_API_URL_ID, tokenDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTokenToMatchAllProperties(updatedToken);
    }

    @Test
    @Transactional
    void putNonExistingToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, tokenDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateTokenWithPatch() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        long databaseSizeBeforeUpdate = getRepositoryCount();

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
            .createdBy(UPDATED_CREATED_BY);

        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedToken))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTokenUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedToken, token), getPersistedToken(token));
    }

    @Test
    @Transactional
    void fullUpdateTokenWithPatch() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        long databaseSizeBeforeUpdate = getRepositoryCount();

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
                    .content(om.writeValueAsBytes(partialUpdatedToken))
            )
            .andExpect(status().isOk());

        // Validate the Token in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTokenUpdatableFieldsEquals(partialUpdatedToken, getPersistedToken(partialUpdatedToken));
    }

    @Test
    @Transactional
    void patchNonExistingToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, tokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(tokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamToken() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        token.setId(longCount.incrementAndGet());

        // Create the Token
        TokenDTO tokenDTO = tokenMapper.toDto(token);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restTokenMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(tokenDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Token in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteToken() throws Exception {
        // Initialize the database
        insertedToken = tokenRepository.saveAndFlush(token);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the token
        restTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, token.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tokenRepository.count();
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

    protected Token getPersistedToken(Token token) {
        return tokenRepository.findById(token.getId()).orElseThrow();
    }

    protected void assertPersistedTokenToMatchAllProperties(Token expectedToken) {
        assertTokenAllPropertiesEquals(expectedToken, getPersistedToken(expectedToken));
    }

    protected void assertPersistedTokenToMatchUpdatableProperties(Token expectedToken) {
        assertTokenAllUpdatablePropertiesEquals(expectedToken, getPersistedToken(expectedToken));
    }
}

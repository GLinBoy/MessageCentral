package com.glinboy.app.web.rest;

import static com.glinboy.app.domain.NotificationDataAsserts.*;
import static com.glinboy.app.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.repository.NotificationDataRepository;
import jakarta.persistence.EntityManager;
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
 * Integration tests for the {@link NotificationDataResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class NotificationDataResourceIT {

    private static final String DEFAULT_DATA_KEY = "AAAAAAAAAA";
    private static final String UPDATED_DATA_KEY = "BBBBBBBBBB";

    private static final String DEFAULT_DATA_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_DATA_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notification-data";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationDataRepository notificationDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationDataMockMvc;

    private NotificationData notificationData;

    private NotificationData insertedNotificationData;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationData createEntity(EntityManager em) {
        NotificationData notificationData = new NotificationData().dataKey(DEFAULT_DATA_KEY).dataValue(DEFAULT_DATA_VALUE);
        // Add required entity
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            notification = NotificationResourceIT.createEntity();
            em.persist(notification);
            em.flush();
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        notificationData.setNotification(notification);
        return notificationData;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static NotificationData createUpdatedEntity(EntityManager em) {
        NotificationData updatedNotificationData = new NotificationData().dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);
        // Add required entity
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            notification = NotificationResourceIT.createUpdatedEntity();
            em.persist(notification);
            em.flush();
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        updatedNotificationData.setNotification(notification);
        return updatedNotificationData;
    }

    @BeforeEach
    public void initTest() {
        notificationData = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedNotificationData != null) {
            notificationDataRepository.delete(insertedNotificationData);
            insertedNotificationData = null;
        }
    }

    @Test
    @Transactional
    void createNotificationData() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the NotificationData
        var returnedNotificationData = om.readValue(
            restNotificationDataMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationData)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            NotificationData.class
        );

        // Validate the NotificationData in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertNotificationDataUpdatableFieldsEquals(returnedNotificationData, getPersistedNotificationData(returnedNotificationData));

        insertedNotificationData = returnedNotificationData;
    }

    @Test
    @Transactional
    void createNotificationDataWithExistingId() throws Exception {
        // Create the NotificationData with an existing ID
        notificationData.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationData)))
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDataKeyIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationData.setDataKey(null);

        // Create the NotificationData, which fails.

        restNotificationDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationData)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataValueIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notificationData.setDataValue(null);

        // Create the NotificationData, which fails.

        restNotificationDataMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationData)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationData() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        // Get all the notificationDataList
        restNotificationDataMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(notificationData.getId().intValue())))
            .andExpect(jsonPath("$.[*].dataKey").value(hasItem(DEFAULT_DATA_KEY)))
            .andExpect(jsonPath("$.[*].dataValue").value(hasItem(DEFAULT_DATA_VALUE)));
    }

    @Test
    @Transactional
    void getNotificationData() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        // Get the notificationData
        restNotificationDataMockMvc
            .perform(get(ENTITY_API_URL_ID, notificationData.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(notificationData.getId().intValue()))
            .andExpect(jsonPath("$.dataKey").value(DEFAULT_DATA_KEY))
            .andExpect(jsonPath("$.dataValue").value(DEFAULT_DATA_VALUE));
    }

    @Test
    @Transactional
    void getNonExistingNotificationData() throws Exception {
        // Get the notificationData
        restNotificationDataMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingNotificationData() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationData
        NotificationData updatedNotificationData = notificationDataRepository.findById(notificationData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationData are not directly saved in db
        em.detach(updatedNotificationData);
        updatedNotificationData.dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);

        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNotificationData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationDataToMatchAllProperties(updatedNotificationData);
    }

    @Test
    @Transactional
    void putNonExistingNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(notificationData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationDataWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationData using partial update
        NotificationData partialUpdatedNotificationData = new NotificationData();
        partialUpdatedNotificationData.setId(notificationData.getId());

        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationDataUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotificationData, notificationData),
            getPersistedNotificationData(notificationData)
        );
    }

    @Test
    @Transactional
    void fullUpdateNotificationDataWithPatch() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notificationData using partial update
        NotificationData partialUpdatedNotificationData = new NotificationData();
        partialUpdatedNotificationData.setId(notificationData.getId());

        partialUpdatedNotificationData.dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);

        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationDataUpdatableFieldsEquals(
            partialUpdatedNotificationData,
            getPersistedNotificationData(partialUpdatedNotificationData)
        );
    }

    @Test
    @Transactional
    void patchNonExistingNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationData() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(notificationData)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationData in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationData() throws Exception {
        // Initialize the database
        insertedNotificationData = notificationDataRepository.saveAndFlush(notificationData);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notificationData
        restNotificationDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationDataRepository.count();
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

    protected NotificationData getPersistedNotificationData(NotificationData notificationData) {
        return notificationDataRepository.findById(notificationData.getId()).orElseThrow();
    }

    protected void assertPersistedNotificationDataToMatchAllProperties(NotificationData expectedNotificationData) {
        assertNotificationDataAllPropertiesEquals(expectedNotificationData, getPersistedNotificationData(expectedNotificationData));
    }

    protected void assertPersistedNotificationDataToMatchUpdatableProperties(NotificationData expectedNotificationData) {
        assertNotificationDataAllUpdatablePropertiesEquals(
            expectedNotificationData,
            getPersistedNotificationData(expectedNotificationData)
        );
    }
}

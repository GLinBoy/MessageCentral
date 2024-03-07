package com.glinboy.app.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.glinboy.app.IntegrationTest;
import com.glinboy.app.domain.Notification;
import com.glinboy.app.domain.NotificationData;
import com.glinboy.app.repository.NotificationDataRepository;
import jakarta.persistence.EntityManager;
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
    private NotificationDataRepository notificationDataRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restNotificationDataMockMvc;

    private NotificationData notificationData;

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
            notification = NotificationResourceIT.createEntity(em);
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
        NotificationData notificationData = new NotificationData().dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);
        // Add required entity
        Notification notification;
        if (TestUtil.findAll(em, Notification.class).isEmpty()) {
            notification = NotificationResourceIT.createUpdatedEntity(em);
            em.persist(notification);
            em.flush();
        } else {
            notification = TestUtil.findAll(em, Notification.class).get(0);
        }
        notificationData.setNotification(notification);
        return notificationData;
    }

    @BeforeEach
    public void initTest() {
        notificationData = createEntity(em);
    }

    @Test
    @Transactional
    void createNotificationData() throws Exception {
        int databaseSizeBeforeCreate = notificationDataRepository.findAll().size();
        // Create the NotificationData
        restNotificationDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isCreated());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeCreate + 1);
        NotificationData testNotificationData = notificationDataList.get(notificationDataList.size() - 1);
        assertThat(testNotificationData.getDataKey()).isEqualTo(DEFAULT_DATA_KEY);
        assertThat(testNotificationData.getDataValue()).isEqualTo(DEFAULT_DATA_VALUE);
    }

    @Test
    @Transactional
    void createNotificationDataWithExistingId() throws Exception {
        // Create the NotificationData with an existing ID
        notificationData.setId(1L);

        int databaseSizeBeforeCreate = notificationDataRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restNotificationDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkDataKeyIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationDataRepository.findAll().size();
        // set the field null
        notificationData.setDataKey(null);

        // Create the NotificationData, which fails.

        restNotificationDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkDataValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = notificationDataRepository.findAll().size();
        // set the field null
        notificationData.setDataValue(null);

        // Create the NotificationData, which fails.

        restNotificationDataMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllNotificationData() throws Exception {
        // Initialize the database
        notificationDataRepository.saveAndFlush(notificationData);

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
        notificationDataRepository.saveAndFlush(notificationData);

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
        notificationDataRepository.saveAndFlush(notificationData);

        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();

        // Update the notificationData
        NotificationData updatedNotificationData = notificationDataRepository.findById(notificationData.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedNotificationData are not directly saved in db
        em.detach(updatedNotificationData);
        updatedNotificationData.dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);

        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedNotificationData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
        NotificationData testNotificationData = notificationDataList.get(notificationDataList.size() - 1);
        assertThat(testNotificationData.getDataKey()).isEqualTo(UPDATED_DATA_KEY);
        assertThat(testNotificationData.getDataValue()).isEqualTo(UPDATED_DATA_VALUE);
    }

    @Test
    @Transactional
    void putNonExistingNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, notificationData.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateNotificationDataWithPatch() throws Exception {
        // Initialize the database
        notificationDataRepository.saveAndFlush(notificationData);

        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();

        // Update the notificationData using partial update
        NotificationData partialUpdatedNotificationData = new NotificationData();
        partialUpdatedNotificationData.setId(notificationData.getId());

        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
        NotificationData testNotificationData = notificationDataList.get(notificationDataList.size() - 1);
        assertThat(testNotificationData.getDataKey()).isEqualTo(DEFAULT_DATA_KEY);
        assertThat(testNotificationData.getDataValue()).isEqualTo(DEFAULT_DATA_VALUE);
    }

    @Test
    @Transactional
    void fullUpdateNotificationDataWithPatch() throws Exception {
        // Initialize the database
        notificationDataRepository.saveAndFlush(notificationData);

        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();

        // Update the notificationData using partial update
        NotificationData partialUpdatedNotificationData = new NotificationData();
        partialUpdatedNotificationData.setId(notificationData.getId());

        partialUpdatedNotificationData.dataKey(UPDATED_DATA_KEY).dataValue(UPDATED_DATA_VALUE);

        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedNotificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedNotificationData))
            )
            .andExpect(status().isOk());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
        NotificationData testNotificationData = notificationDataList.get(notificationDataList.size() - 1);
        assertThat(testNotificationData.getDataKey()).isEqualTo(UPDATED_DATA_KEY);
        assertThat(testNotificationData.getDataValue()).isEqualTo(UPDATED_DATA_VALUE);
    }

    @Test
    @Transactional
    void patchNonExistingNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, notificationData.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isBadRequest());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamNotificationData() throws Exception {
        int databaseSizeBeforeUpdate = notificationDataRepository.findAll().size();
        notificationData.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restNotificationDataMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(notificationData))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the NotificationData in the database
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteNotificationData() throws Exception {
        // Initialize the database
        notificationDataRepository.saveAndFlush(notificationData);

        int databaseSizeBeforeDelete = notificationDataRepository.findAll().size();

        // Delete the notificationData
        restNotificationDataMockMvc
            .perform(delete(ENTITY_API_URL_ID, notificationData.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<NotificationData> notificationDataList = notificationDataRepository.findAll();
        assertThat(notificationDataList).hasSize(databaseSizeBeforeDelete - 1);
    }
}

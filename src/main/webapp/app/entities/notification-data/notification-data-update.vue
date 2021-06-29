<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notificationData.home.createOrEditLabel"
          data-cy="NotificationDataCreateUpdateHeading"
          v-text="$t('messageCentralApp.notificationData.home.createOrEditLabel')"
        >
          Create or edit a NotificationData
        </h2>
        <div>
          <div class="form-group" v-if="notificationData.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="notificationData.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notificationData.key')" for="notification-data-key">Key</label>
            <input
              type="text"
              class="form-control"
              name="key"
              id="notification-data-key"
              data-cy="key"
              :class="{ valid: !$v.notificationData.key.$invalid, invalid: $v.notificationData.key.$invalid }"
              v-model="$v.notificationData.key.$model"
              required
            />
            <div v-if="$v.notificationData.key.$anyDirty && $v.notificationData.key.$invalid">
              <small class="form-text text-danger" v-if="!$v.notificationData.key.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notificationData.key.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notificationData.value')" for="notification-data-value"
              >Value</label
            >
            <input
              type="text"
              class="form-control"
              name="value"
              id="notification-data-value"
              data-cy="value"
              :class="{ valid: !$v.notificationData.value.$invalid, invalid: $v.notificationData.value.$invalid }"
              v-model="$v.notificationData.value.$model"
              required
            />
            <div v-if="$v.notificationData.value.$anyDirty && $v.notificationData.value.$invalid">
              <small class="form-text text-danger" v-if="!$v.notificationData.value.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notificationData.value.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 256 })"
              >
                This field cannot be longer than 256 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="$t('messageCentralApp.notificationData.notification')"
              for="notification-data-notification"
              >Notification</label
            >
            <select
              class="form-control"
              id="notification-data-notification"
              data-cy="notification"
              name="notification"
              v-model="notificationData.notification"
              required
            >
              <option v-if="!notificationData.notification" v-bind:value="null" selected></option>
              <option
                v-bind:value="
                  notificationData.notification && notificationOption.id === notificationData.notification.id
                    ? notificationData.notification
                    : notificationOption
                "
                v-for="notificationOption in notifications"
                :key="notificationOption.id"
              >
                {{ notificationOption.id }}
              </option>
            </select>
          </div>
          <div v-if="$v.notificationData.notification.$anyDirty && $v.notificationData.notification.$invalid">
            <small
              class="form-text text-danger"
              v-if="!$v.notificationData.notification.required"
              v-text="$t('entity.validation.required')"
            >
              This field is required.
            </small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.notificationData.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./notification-data-update.component.ts"></script>

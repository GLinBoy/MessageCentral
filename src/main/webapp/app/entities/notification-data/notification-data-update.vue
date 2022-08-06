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
            <label class="form-control-label" v-text="$t('messageCentralApp.notificationData.dataKey')" for="notification-data-dataKey"
              >Data Key</label
            >
            <input
              type="text"
              class="form-control"
              name="dataKey"
              id="notification-data-dataKey"
              data-cy="dataKey"
              :class="{ valid: !$v.notificationData.dataKey.$invalid, invalid: $v.notificationData.dataKey.$invalid }"
              v-model="$v.notificationData.dataKey.$model"
              required
            />
            <div v-if="$v.notificationData.dataKey.$anyDirty && $v.notificationData.dataKey.$invalid">
              <small class="form-text text-danger" v-if="!$v.notificationData.dataKey.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notificationData.dataKey.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notificationData.dataValue')" for="notification-data-dataValue"
              >Data Value</label
            >
            <input
              type="text"
              class="form-control"
              name="dataValue"
              id="notification-data-dataValue"
              data-cy="dataValue"
              :class="{ valid: !$v.notificationData.dataValue.$invalid, invalid: $v.notificationData.dataValue.$invalid }"
              v-model="$v.notificationData.dataValue.$model"
              required
            />
            <div v-if="$v.notificationData.dataValue.$anyDirty && $v.notificationData.dataValue.$invalid">
              <small class="form-text text-danger" v-if="!$v.notificationData.dataValue.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notificationData.dataValue.maxLength"
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
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
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

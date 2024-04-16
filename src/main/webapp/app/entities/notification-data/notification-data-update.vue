<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notificationData.home.createOrEditLabel"
          data-cy="NotificationDataCreateUpdateHeading"
          v-text="t$('messageCentralApp.notificationData.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="notificationData.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="notificationData.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('messageCentralApp.notificationData.dataKey')"
              for="notification-data-dataKey"
            ></label>
            <input
              type="text"
              class="form-control"
              name="dataKey"
              id="notification-data-dataKey"
              data-cy="dataKey"
              :class="{ valid: !v$.dataKey.$invalid, invalid: v$.dataKey.$invalid }"
              v-model="v$.dataKey.$model"
              required
            />
            <div v-if="v$.dataKey.$anyDirty && v$.dataKey.$invalid">
              <small class="form-text text-danger" v-for="error of v$.dataKey.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('messageCentralApp.notificationData.dataValue')"
              for="notification-data-dataValue"
            ></label>
            <input
              type="text"
              class="form-control"
              name="dataValue"
              id="notification-data-dataValue"
              data-cy="dataValue"
              :class="{ valid: !v$.dataValue.$invalid, invalid: v$.dataValue.$invalid }"
              v-model="v$.dataValue.$model"
              required
            />
            <div v-if="v$.dataValue.$anyDirty && v$.dataValue.$invalid">
              <small class="form-text text-danger" v-for="error of v$.dataValue.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('messageCentralApp.notificationData.notification')"
              for="notification-data-notification"
            ></label>
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
          <div v-if="v$.notification.$anyDirty && v$.notification.$invalid">
            <small class="form-text text-danger" v-for="error of v$.notification.$errors" :key="error.$uid">{{ error.$message }}</small>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./notification-data-update.component.ts"></script>

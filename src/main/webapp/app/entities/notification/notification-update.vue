<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notification.home.createOrEditLabel"
          data-cy="NotificationCreateUpdateHeading"
          v-text="t$('messageCentralApp.notification.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="notification.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="notification.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.username')" for="notification-username"></label>
            <input
              type="text"
              class="form-control"
              name="username"
              id="notification-username"
              data-cy="username"
              :class="{ valid: !v$.username.$invalid, invalid: v$.username.$invalid }"
              v-model="v$.username.$model"
              required
            />
            <div v-if="v$.username.$anyDirty && v$.username.$invalid">
              <small class="form-text text-danger" v-for="error of v$.username.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.token')" for="notification-token"></label>
            <input
              type="text"
              class="form-control"
              name="token"
              id="notification-token"
              data-cy="token"
              :class="{ valid: !v$.token.$invalid, invalid: v$.token.$invalid }"
              v-model="v$.token.$model"
              required
            />
            <div v-if="v$.token.$anyDirty && v$.token.$invalid">
              <small class="form-text text-danger" v-for="error of v$.token.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.subject')" for="notification-subject"></label>
            <input
              type="text"
              class="form-control"
              name="subject"
              id="notification-subject"
              data-cy="subject"
              :class="{ valid: !v$.subject.$invalid, invalid: v$.subject.$invalid }"
              v-model="v$.subject.$model"
              required
            />
            <div v-if="v$.subject.$anyDirty && v$.subject.$invalid">
              <small class="form-text text-danger" v-for="error of v$.subject.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.content')" for="notification-content"></label>
            <input
              type="text"
              class="form-control"
              name="content"
              id="notification-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              required
            />
            <div v-if="v$.content.$anyDirty && v$.content.$invalid">
              <small class="form-text text-danger" v-for="error of v$.content.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.image')" for="notification-image"></label>
            <input
              type="text"
              class="form-control"
              name="image"
              id="notification-image"
              data-cy="image"
              :class="{ valid: !v$.image.$invalid, invalid: v$.image.$invalid }"
              v-model="v$.image.$model"
            />
            <div v-if="v$.image.$anyDirty && v$.image.$invalid">
              <small class="form-text text-danger" v-for="error of v$.image.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.status')" for="notification-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="notification-status"
              data-cy="status"
            >
              <option
                v-for="messageStatus in messageStatusValues"
                :key="messageStatus"
                v-bind:value="messageStatus"
                v-bind:label="t$('messageCentralApp.MessageStatus.' + messageStatus)"
              >
                {{ messageStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.createdAt')" for="notification-createdAt"></label>
            <div class="d-flex">
              <input
                id="notification-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !v$.createdAt.$invalid, invalid: v$.createdAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="v$.createdAt.$anyDirty && v$.createdAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.createdBy')" for="notification-createdBy"></label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="notification-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
              required
            />
            <div v-if="v$.createdBy.$anyDirty && v$.createdBy.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdBy.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./notification-update.component.ts"></script>

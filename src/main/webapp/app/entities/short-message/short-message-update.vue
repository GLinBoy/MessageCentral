<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.shortMessage.home.createOrEditLabel"
          data-cy="ShortMessageCreateUpdateHeading"
          v-text="$t('messageCentralApp.shortMessage.home.createOrEditLabel')"
        >
          Create or edit a ShortMessage
        </h2>
        <div>
          <div class="form-group" v-if="shortMessage.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="shortMessage.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessage.phoneNumber')" for="short-message-phoneNumber"
              >Phone Number</label
            >
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="short-message-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !$v.shortMessage.phoneNumber.$invalid, invalid: $v.shortMessage.phoneNumber.$invalid }"
              v-model="$v.shortMessage.phoneNumber.$model"
              required
            />
            <div v-if="$v.shortMessage.phoneNumber.$anyDirty && $v.shortMessage.phoneNumber.$invalid">
              <small class="form-text text-danger" v-if="!$v.shortMessage.phoneNumber.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.phoneNumber.minLength"
                v-text="$t('entity.validation.minlength', { min: 7 })"
              >
                This field is required to be at least 7 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.phoneNumber.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 15 })"
              >
                This field cannot be longer than 15 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.phoneNumber.pattern"
                v-text="$t('entity.validation.pattern', { pattern: 'Phone Number' })"
              >
                This field should follow pattern for "Phone Number".
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessage.content')" for="short-message-content"
              >Content</label
            >
            <input
              type="text"
              class="form-control"
              name="content"
              id="short-message-content"
              data-cy="content"
              :class="{ valid: !$v.shortMessage.content.$invalid, invalid: $v.shortMessage.content.$invalid }"
              v-model="$v.shortMessage.content.$model"
              required
            />
            <div v-if="$v.shortMessage.content.$anyDirty && $v.shortMessage.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.shortMessage.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.content.minLength"
                v-text="$t('entity.validation.minlength', { min: 6 })"
              >
                This field is required to be at least 6 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.content.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 160 })"
              >
                This field cannot be longer than 160 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessage.status')" for="short-message-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.shortMessage.status.$invalid, invalid: $v.shortMessage.status.$invalid }"
              v-model="$v.shortMessage.status.$model"
              id="short-message-status"
              data-cy="status"
            >
              <option
                v-for="messageStatus in messageStatusValues"
                :key="messageStatus"
                v-bind:value="messageStatus"
                v-bind:label="$t('messageCentralApp.MessageStatus.' + messageStatus)"
              >
                {{ messageStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessage.createdAt')" for="short-message-createdAt"
              >Created At</label
            >
            <div class="d-flex">
              <input
                id="short-message-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !$v.shortMessage.createdAt.$invalid, invalid: $v.shortMessage.createdAt.$invalid }"
                required
                :value="convertDateTimeFromServer($v.shortMessage.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="$v.shortMessage.createdAt.$anyDirty && $v.shortMessage.createdAt.$invalid">
              <small class="form-text text-danger" v-if="!$v.shortMessage.createdAt.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessage.createdAt.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessage.createdBy')" for="short-message-createdBy"
              >Created By</label
            >
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="short-message-createdBy"
              data-cy="createdBy"
              :class="{ valid: !$v.shortMessage.createdBy.$invalid, invalid: $v.shortMessage.createdBy.$invalid }"
              v-model="$v.shortMessage.createdBy.$model"
              required
            />
            <div v-if="$v.shortMessage.createdBy.$anyDirty && $v.shortMessage.createdBy.$invalid">
              <small class="form-text text-danger" v-if="!$v.shortMessage.createdBy.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
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
            :disabled="$v.shortMessage.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./short-message-update.component.ts"></script>

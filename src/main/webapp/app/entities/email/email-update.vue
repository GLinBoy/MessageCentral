<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.email.home.createOrEditLabel"
          data-cy="EmailCreateUpdateHeading"
          v-text="$t('messageCentralApp.email.home.createOrEditLabel')"
        >
          Create or edit a Email
        </h2>
        <div>
          <div class="form-group" v-if="email.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="email.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.email.receiver')" for="email-receiver">Receiver</label>
            <input
              type="text"
              class="form-control"
              name="receiver"
              id="email-receiver"
              data-cy="receiver"
              :class="{ valid: !$v.email.receiver.$invalid, invalid: $v.email.receiver.$invalid }"
              v-model="$v.email.receiver.$model"
              required
            />
            <div v-if="$v.email.receiver.$anyDirty && $v.email.receiver.$invalid">
              <small class="form-text text-danger" v-if="!$v.email.receiver.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.email.receiver.minLength"
                v-text="$t('entity.validation.minlength', { min: 8 })"
              >
                This field is required to be at least 8 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.email.receiver.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.email.receiver.pattern"
                v-text="$t('entity.validation.pattern', { pattern: 'Receiver' })"
              >
                This field should follow pattern for "Receiver".
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.email.subject')" for="email-subject">Subject</label>
            <input
              type="text"
              class="form-control"
              name="subject"
              id="email-subject"
              data-cy="subject"
              :class="{ valid: !$v.email.subject.$invalid, invalid: $v.email.subject.$invalid }"
              v-model="$v.email.subject.$model"
              required
            />
            <div v-if="$v.email.subject.$anyDirty && $v.email.subject.$invalid">
              <small class="form-text text-danger" v-if="!$v.email.subject.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.email.subject.minLength"
                v-text="$t('entity.validation.minlength', { min: 4 })"
              >
                This field is required to be at least 4 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.email.subject.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.email.content')" for="email-content">Content</label>
            <textarea
              class="form-control"
              name="content"
              id="email-content"
              data-cy="content"
              :class="{ valid: !$v.email.content.$invalid, invalid: $v.email.content.$invalid }"
              v-model="$v.email.content.$model"
              required
            ></textarea>
            <div v-if="$v.email.content.$anyDirty && $v.email.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.email.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.email.status')" for="email-status">Status</label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !$v.email.status.$invalid, invalid: $v.email.status.$invalid }"
              v-model="$v.email.status.$model"
              id="email-status"
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
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.email.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./email-update.component.ts"></script>

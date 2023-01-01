<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.emails.home.createLabel"
          data-cy="EmailCreateHeading"
          v-text="$t('messageCentralApp.emails.home.createLabel')"
        >
          Create an Email(s)
        </h2>
        <div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.emails.receivers')" for="email-receivers">Receivers</label>
            <b-form-tags
              type="text"
              separator=" ,;"
              name="receivers"
              id="email-receivers"
              class="form-control"
              data-cy="receivers"
              :placeholder="$t('messageCentralApp.emails.placeholder.newEmail')"
              invalid-tag-text="invalid email(s)"
              :tag-validator="emailValidator"
              remove-on-delete
              no-add-on-enter
              required
              v-model="$v.emails.receivers.$model"
              :class="{ valid: !$v.emails.receivers.$invalid, invalid: $v.emails.receivers.$invalid }"
              :input-attrs="{ 'aria-describedby': 'emails-separate-by-help' }"
            />
            <b-form-text
              id="emails-separate-by-help"
              class="mt-2 form-text text-muted font-weight-lighter"
              v-html="$t('messageCentralApp.emails.hint.newEmail')"
            >
              💡 Separate by <kbd>Space</kbd> or <kbd>,</kbd> or <kbd>;</kbd>
            </b-form-text>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.emails.subject')" for="email-subject">Subject</label>
            <input
              type="text"
              class="form-control"
              name="subject"
              id="email-subject"
              data-cy="subject"
              :class="{ valid: !$v.emails.subject.$invalid, invalid: $v.emails.subject.$invalid }"
              v-model="$v.emails.subject.$model"
              required
            />
            <div v-if="$v.emails.subject.$anyDirty && $v.emails.subject.$invalid">
              <small class="form-text text-danger" v-if="!$v.emails.subject.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.emails.subject.minLength"
                v-text="$t('entity.validation.minlength', { min: 4 })"
              >
                This field is required to be at least 4 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.emails.subject.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.emails.content')" for="email-content">Content</label>
            <textarea
              class="form-control"
              name="content"
              id="email-content"
              data-cy="content"
              :class="{ valid: !$v.emails.content.$invalid, invalid: $v.emails.content.$invalid }"
              v-model="$v.emails.content.$model"
              required
            ></textarea>
            <div v-if="$v.emails.content.$anyDirty && $v.emails.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.emails.content.required" v-text="$t('entity.validation.required')">
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
            :disabled="$v.emails.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.send')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./email-multiple.component.ts"></script>

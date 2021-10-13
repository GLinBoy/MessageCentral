<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.shortMessages.home.createLabel"
          data-cy="ShortMessagesCreateHeading"
          v-text="$t('messageCentralApp.shortMessages.home.createLabel')"
        >
          Create or edit a ShortMessage
        </h2>
        <div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessages.phoneNumbers')" for="short-message-phoneNumbers"
              >Phone Number</label
            >
            <b-form-tags
              type="text"
              separator=" ,;"
              name="phoneNumbers"
              id="short-message-phoneNumbers"
              class="form-control"
              data-cy="phoneNumbers"
              placeholder="new number"
              invalid-tag-text="invalid Number(s)"
              :tag-validator="numberValidator"
              remove-on-delete
              no-add-on-enter
              required
              v-model="$v.shortMessages.phoneNumbers.$model"
              :class="{ valid: !$v.shortMessages.phoneNumbers.$invalid, invalid: $v.shortMessages.phoneNumbers.$invalid }"
              :input-attrs="{ 'aria-describedby': 'numbers-seprate-by-help' }"
            />
            <b-form-text name="numbers-seprate-by-help" id="numbers-seprate-by-help" class="mt-2 form-text text-muted font-weight-lighter">
              💡 Seprate by <kbd>Space</kbd> or <kbd>,</kbd> or <kbd>;</kbd>
            </b-form-text>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.shortMessages.content')" for="short-message-content"
              >Content</label
            >
            <textarea
              class="form-control"
              name="content"
              id="short-message-content"
              data-cy="content"
              :class="{ valid: !$v.shortMessages.content.$invalid, invalid: $v.shortMessages.content.$invalid }"
              v-model="$v.shortMessages.content.$model"
            ></textarea>
            <div v-if="$v.shortMessages.content.$anyDirty && $v.shortMessages.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.shortMessages.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessages.content.minLength"
                v-text="$t('entity.validation.minlength', { min: 6 })"
              >
                This field is required to be at least 6 characters.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.shortMessages.content.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 160 })"
              >
                This field cannot be longer than 160 characters.
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
            :disabled="$v.shortMessages.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./short-message-multiple.component.ts"></script>

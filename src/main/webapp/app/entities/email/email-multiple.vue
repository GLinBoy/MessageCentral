<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.emails.home.createLabel"
          data-cy="EmailCreateHeading"
          v-text="t$('messageCentralApp.emails.home.createLabel')"
        />
        <div>
          <div class="form-group">
            <vue3-tags-input :tags="v$.receivers.$model" @on-tags-changed="handleChangeTag" :validate="emailValidator" />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.emails.subject')" for="email-subject" />
            <input
              type="text"
              class="form-control"
              name="subject"
              id="email-subject"
              data-cy="subject"
              :class="{ valid: !v$.subject.$invalid, invalid: v$.subject.$invalid }"
              v-model="v$.subject.$model"
              required
            />
            <div v-if="v$.subject.$anyDirty && v$.subject.$invalid">
              <small class="form-text text-danger" v-if="!v$.subject.required" v-text="t$('entity.validation.required')" />
              <small class="form-text text-danger" v-if="!v$.subject.minLength" v-text="t$('entity.validation.minlength', { min: 4 })" />
              <small class="form-text text-danger" v-if="!v$.subject.maxLength" v-text="t$('entity.validation.maxlength', { max: 128 })" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.emails.content')" for="email-content" />
            <textarea
              class="form-control"
              name="content"
              id="email-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              required
            ></textarea>
            <div v-if="v$.content.$anyDirty && v$.content.$invalid">
              <small class="form-text text-danger" v-if="!v$.content.required" v-text="t$('entity.validation.required')" />
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.emails.emailType')" for="email-type" />
            <b-form-select v-model="v$.emailType.$model" :options="emailTypeValues"></b-form-select>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')" />
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.send')" />
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script lang="ts" src="./email-multiple.component.ts"></script>

<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.shortMessage.home.createOrEditLabel"
          data-cy="ShortMessageCreateUpdateHeading"
          v-text="t$('messageCentralApp.shortMessage.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group">
            <vue3-tags-input :tags="v$.phoneNumbers.$model" @on-tags-changed="handleChangeTag" />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.shortMessage.content')" for="short-message-content"></label>
            <textarea
              class="form-control"
              name="content"
              id="short-message-content"
              data-cy="content"
              :class="{ valid: !v$.content.$invalid, invalid: v$.content.$invalid }"
              v-model="v$.content.$model"
              required
            />
            <div v-if="v$.content.$anyDirty && v$.content.$invalid">
              <small class="form-text text-danger" v-for="error of v$.content.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./short-message-multiple.component.ts"></script>

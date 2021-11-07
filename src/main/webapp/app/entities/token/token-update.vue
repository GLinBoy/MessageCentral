<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.token.home.createOrEditLabel"
          data-cy="TokenCreateUpdateHeading"
          v-text="$t('messageCentralApp.token.home.createOrEditLabel')"
        >
          Create or edit a Token
        </h2>
        <div>
          <div class="form-group" v-if="token.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="token.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.name')" for="token-name">Name</label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="token-name"
              data-cy="name"
              :class="{ valid: !$v.token.name.$invalid, invalid: $v.token.name.$invalid }"
              v-model="$v.token.name.$model"
              required
            />
            <div v-if="$v.token.name.$anyDirty && $v.token.name.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.name.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.token.name.maxLength" v-text="$t('entity.validation.maxlength', { max: 64 })">
                This field cannot be longer than 64 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.disable')" for="token-disable">Disable</label>
            <input
              type="checkbox"
              class="form-check"
              name="disable"
              id="token-disable"
              data-cy="disable"
              :class="{ valid: !$v.token.disable.$invalid, invalid: $v.token.disable.$invalid }"
              v-model="$v.token.disable.$model"
              required
            />
            <div v-if="$v.token.disable.$anyDirty && $v.token.disable.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.disable.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.deprecateAt')" for="token-deprecateAt"
              >Deprecate At</label
            >
            <div class="d-flex">
              <input
                id="token-deprecateAt"
                data-cy="deprecateAt"
                type="datetime-local"
                class="form-control"
                name="deprecateAt"
                :class="{ valid: !$v.token.deprecateAt.$invalid, invalid: $v.token.deprecateAt.$invalid }"
                required
                :value="convertDateTimeFromServer($v.token.deprecateAt.$model)"
                @change="updateInstantField('deprecateAt', $event)"
              />
            </div>
            <div v-if="$v.token.deprecateAt.$anyDirty && $v.token.deprecateAt.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.deprecateAt.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.token.deprecateAt.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.roles')" for="token-roles">Roles</label>
            <input
              type="number"
              class="form-control"
              name="roles"
              id="token-roles"
              data-cy="roles"
              :class="{ valid: !$v.token.roles.$invalid, invalid: $v.token.roles.$invalid }"
              v-model.number="$v.token.roles.$model"
              required
            />
            <div v-if="$v.token.roles.$anyDirty && $v.token.roles.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.roles.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small class="form-text text-danger" v-if="!$v.token.roles.numeric" v-text="$t('entity.validation.number')">
                This field should be a number.
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
            :disabled="$v.token.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./token-update.component.ts"></script>

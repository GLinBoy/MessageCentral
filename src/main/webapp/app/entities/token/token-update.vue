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
            <label class="form-control-label" v-text="$t('messageCentralApp.token.token')" for="token-token">Token</label>
            <input
              type="text"
              class="form-control"
              name="token"
              id="token-token"
              data-cy="token"
              :class="{ valid: !$v.token.token.$invalid, invalid: $v.token.token.$invalid }"
              v-model="$v.token.token.$model"
              required
            />
            <div v-if="$v.token.token.$anyDirty && $v.token.token.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.token.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.token.token.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 512 })"
              >
                This field cannot be longer than 512 characters.
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
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.createdAt')" for="token-createdAt">Created At</label>
            <div class="d-flex">
              <input
                id="token-createdAt"
                data-cy="createdAt"
                type="datetime-local"
                class="form-control"
                name="createdAt"
                :class="{ valid: !$v.token.createdAt.$invalid, invalid: $v.token.createdAt.$invalid }"
                required
                :value="convertDateTimeFromServer($v.token.createdAt.$model)"
                @change="updateInstantField('createdAt', $event)"
              />
            </div>
            <div v-if="$v.token.createdAt.$anyDirty && $v.token.createdAt.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.createdAt.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.token.createdAt.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.createdBy')" for="token-createdBy">Created By</label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="token-createdBy"
              data-cy="createdBy"
              :class="{ valid: !$v.token.createdBy.$invalid, invalid: $v.token.createdBy.$invalid }"
              v-model="$v.token.createdBy.$model"
              required
            />
            <div v-if="$v.token.createdBy.$anyDirty && $v.token.createdBy.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.createdBy.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.updatedAt')" for="token-updatedAt">Updated At</label>
            <div class="d-flex">
              <input
                id="token-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !$v.token.updatedAt.$invalid, invalid: $v.token.updatedAt.$invalid }"
                required
                :value="convertDateTimeFromServer($v.token.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
            <div v-if="$v.token.updatedAt.$anyDirty && $v.token.updatedAt.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.updatedAt.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.token.updatedAt.ZonedDateTimelocal"
                v-text="$t('entity.validation.ZonedDateTimelocal')"
              >
                This field should be a date and time.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.token.updatedBy')" for="token-updatedBy">Updated By</label>
            <input
              type="text"
              class="form-control"
              name="updatedBy"
              id="token-updatedBy"
              data-cy="updatedBy"
              :class="{ valid: !$v.token.updatedBy.$invalid, invalid: $v.token.updatedBy.$invalid }"
              v-model="$v.token.updatedBy.$model"
              required
            />
            <div v-if="$v.token.updatedBy.$anyDirty && $v.token.updatedBy.$invalid">
              <small class="form-text text-danger" v-if="!$v.token.updatedBy.required" v-text="$t('entity.validation.required')">
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

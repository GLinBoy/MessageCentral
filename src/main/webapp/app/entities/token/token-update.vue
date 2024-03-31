<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.token.home.createOrEditLabel"
          data-cy="TokenCreateUpdateHeading"
          v-text="t$('messageCentralApp.token.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="token.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="token.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.name')" for="token-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="token-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.token')" for="token-token"></label>
            <input
              type="text"
              class="form-control"
              name="token"
              id="token-token"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.token.disable')" for="token-disable"></label>
            <input
              type="checkbox"
              class="form-check"
              name="disable"
              id="token-disable"
              data-cy="disable"
              :class="{ valid: !v$.disable.$invalid, invalid: v$.disable.$invalid }"
              v-model="v$.disable.$model"
              required
            />
            <div v-if="v$.disable.$anyDirty && v$.disable.$invalid">
              <small class="form-text text-danger" v-for="error of v$.disable.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.deprecateAt')" for="token-deprecateAt"></label>
            <div class="d-flex">
              <input
                id="token-deprecateAt"
                data-cy="deprecateAt"
                type="datetime-local"
                class="form-control"
                name="deprecateAt"
                :class="{ valid: !v$.deprecateAt.$invalid, invalid: v$.deprecateAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.deprecateAt.$model)"
                @change="updateInstantField('deprecateAt', $event)"
              />
            </div>
            <div v-if="v$.deprecateAt.$anyDirty && v$.deprecateAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.deprecateAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.roles')" for="token-roles"></label>
            <input
              type="number"
              class="form-control"
              name="roles"
              id="token-roles"
              data-cy="roles"
              :class="{ valid: !v$.roles.$invalid, invalid: v$.roles.$invalid }"
              v-model.number="v$.roles.$model"
              required
            />
            <div v-if="v$.roles.$anyDirty && v$.roles.$invalid">
              <small class="form-text text-danger" v-for="error of v$.roles.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.createdAt')" for="token-createdAt"></label>
            <div class="d-flex">
              <input
                id="token-createdAt"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.token.createdBy')" for="token-createdBy"></label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="token-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
              required
            />
            <div v-if="v$.createdBy.$anyDirty && v$.createdBy.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdBy.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.updatedAt')" for="token-updatedAt"></label>
            <div class="d-flex">
              <input
                id="token-updatedAt"
                data-cy="updatedAt"
                type="datetime-local"
                class="form-control"
                name="updatedAt"
                :class="{ valid: !v$.updatedAt.$invalid, invalid: v$.updatedAt.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.updatedAt.$model)"
                @change="updateInstantField('updatedAt', $event)"
              />
            </div>
            <div v-if="v$.updatedAt.$anyDirty && v$.updatedAt.$invalid">
              <small class="form-text text-danger" v-for="error of v$.updatedAt.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.token.updatedBy')" for="token-updatedBy"></label>
            <input
              type="text"
              class="form-control"
              name="updatedBy"
              id="token-updatedBy"
              data-cy="updatedBy"
              :class="{ valid: !v$.updatedBy.$invalid, invalid: v$.updatedBy.$invalid }"
              v-model="v$.updatedBy.$model"
              required
            />
            <div v-if="v$.updatedBy.$anyDirty && v$.updatedBy.$invalid">
              <small class="form-text text-danger" v-for="error of v$.updatedBy.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./token-update.component.ts"></script>

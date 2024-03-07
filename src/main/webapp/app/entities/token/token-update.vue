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
          <div class="row">
            <div class="form-group col-sm-12 col-md-5">
              <label class="form-control-label" v-text="t$('messageCentralApp.token.roles')" for="token-roles"></label>
              <b-form-select
                required
                multiple
                :select-size="3"
                class="form-control"
                name="roles"
                id="token-roles"
                data-cy="roles"
                v-model="userRoleSelected"
                :options="tokenRoleOptions"
                :class="{ valid: !v$.roles.$invalid, invalid: v$.roles.$invalid }"
              />
              <div v-if="v$.roles.$anyDirty && v$.roles.$invalid">
                <small class="form-text text-danger" v-for="error of v$.roles.$errors" :key="error.$uid">{{ error.$message }}</small>
              </div>
            </div>
            <div class="form-group col-sm-12 col-md-5">
              <label class="form-control-label" v-text="t$('messageCentralApp.token.deprecateAt')" for="token-deprecateAt"></label>
              <div class="d-flex flex-column">
                <b-form-select
                  v-model="tokenValidityPeriod"
                  :options="tokenValidityOption"
                  id="token-deprecateAt"
                  data-cy="deprecateAt"
                  class="form-control"
                  name="deprecateAt"
                  :class="{ valid: !v$.deprecateAt.$invalid, invalid: v$.deprecateAt.$invalid }"
                  required
                />
                <div class="mx-2 my-1 font-weight-light text-secondary" v-if="tokenValidityPeriod">
                  <font-awesome-icon icon="calendar" />&nbsp;
                  <span v-text="convertDateTimeToHuman(v$.deprecateAt.$model)" />
                </div>
                <input
                  v-if="false"
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
            <div class="form-group col-sm-12 col-md-2">
              <label class="form-control-label" v-text="t$('messageCentralApp.token.disable')" for="token-disable"></label>
              <b-form-checkbox
                class="form-check"
                name="disable"
                id="token-disable"
                data-cy="disable"
                switch
                required
                size="lg"
                v-model="v$.disable.$model"
                :class="{ valid: !v$.disable.$invalid, invalid: v$.disable.$invalid }"
              />
              <div v-if="v$.disable.$anyDirty && v$.disable.$invalid">
                <small class="form-text text-danger" v-for="error of v$.disable.$errors" :key="error.$uid">{{ error.$message }}</small>
              </div>
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

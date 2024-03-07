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
          <div class="form-group" v-if="shortMessage.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="shortMessage.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('messageCentralApp.shortMessage.phoneNumber')"
              for="short-message-phoneNumber"
            ></label>
            <input
              type="text"
              class="form-control"
              name="phoneNumber"
              id="short-message-phoneNumber"
              data-cy="phoneNumber"
              :class="{ valid: !v$.phoneNumber.$invalid, invalid: v$.phoneNumber.$invalid }"
              v-model="v$.phoneNumber.$model"
              required
            />
            <div v-if="v$.phoneNumber.$anyDirty && v$.phoneNumber.$invalid">
              <small class="form-text text-danger" v-for="error of v$.phoneNumber.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.shortMessage.content')" for="short-message-content"></label>
            <input
              type="text"
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
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.shortMessage.status')" for="short-message-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="short-message-status"
              data-cy="status"
            >
              <option
                v-for="messageStatus in messageStatusValues"
                :key="messageStatus"
                v-bind:value="messageStatus"
                v-bind:label="t$('messageCentralApp.MessageStatus.' + messageStatus)"
              >
                {{ messageStatus }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.shortMessage.createdAt')" for="short-message-createdAt"></label>
            <div class="d-flex">
              <input
                id="short-message-createdAt"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.shortMessage.createdBy')" for="short-message-createdBy"></label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="short-message-createdBy"
              data-cy="createdBy"
              :class="{ valid: !v$.createdBy.$invalid, invalid: v$.createdBy.$invalid }"
              v-model="v$.createdBy.$model"
              required
            />
            <div v-if="v$.createdBy.$anyDirty && v$.createdBy.$invalid">
              <small class="form-text text-danger" v-for="error of v$.createdBy.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./short-message-update.component.ts"></script>

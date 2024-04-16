<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.email.home.createOrEditLabel"
          data-cy="EmailCreateUpdateHeading"
          v-text="t$('messageCentralApp.email.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="email.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="email.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.email.receiver')" for="email-receiver"></label>
            <input
              type="text"
              class="form-control"
              name="receiver"
              id="email-receiver"
              data-cy="receiver"
              :class="{ valid: !v$.receiver.$invalid, invalid: v$.receiver.$invalid }"
              v-model="v$.receiver.$model"
              required
            />
            <div v-if="v$.receiver.$anyDirty && v$.receiver.$invalid">
              <small class="form-text text-danger" v-for="error of v$.receiver.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.email.subject')" for="email-subject"></label>
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
              <small class="form-text text-danger" v-for="error of v$.subject.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.email.content')" for="email-content"></label>
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
              <small class="form-text text-danger" v-for="error of v$.content.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.email.status')" for="email-status"></label>
            <select
              class="form-control"
              name="status"
              :class="{ valid: !v$.status.$invalid, invalid: v$.status.$invalid }"
              v-model="v$.status.$model"
              id="email-status"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.email.emailType')" for="email-emailType"></label>
            <select
              class="form-control"
              name="emailType"
              :class="{ valid: !v$.emailType.$invalid, invalid: v$.emailType.$invalid }"
              v-model="v$.emailType.$model"
              id="email-emailType"
              data-cy="emailType"
            >
              <option
                v-for="emailType in emailTypeValues"
                :key="emailType"
                v-bind:value="emailType"
                v-bind:label="t$('messageCentralApp.EmailType.' + emailType)"
              >
                {{ emailType }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('messageCentralApp.email.createdAt')" for="email-createdAt"></label>
            <div class="d-flex">
              <input
                id="email-createdAt"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.email.createdBy')" for="email-createdBy"></label>
            <input
              type="text"
              class="form-control"
              name="createdBy"
              id="email-createdBy"
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
<script lang="ts" src="./email-update.component.ts"></script>

<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notification.home.createOrEditLabel"
          data-cy="NotificationCreateUpdateHeading"
          v-text="$t('messageCentralApp.notification.home.createOrEditLabel')"
        >
          Create or edit a Notification
        </h2>
        <div>
          <div class="form-group" v-if="notification.id">
            <label for="id" v-text="$t('global.field.id')">ID</label>
            <input type="text" class="form-control" id="id" name="id" v-model="notification.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notification.username')" for="notification-username"
              >Username</label
            >
            <input
              type="text"
              class="form-control"
              name="username"
              id="notification-username"
              data-cy="username"
              :class="{ valid: !$v.notification.username.$invalid, invalid: $v.notification.username.$invalid }"
              v-model="$v.notification.username.$model"
              required
            />
            <div v-if="$v.notification.username.$anyDirty && $v.notification.username.$invalid">
              <small class="form-text text-danger" v-if="!$v.notification.username.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notification.username.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 64 })"
              >
                This field cannot be longer than 64 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notification.token')" for="notification-token">Token</label>
            <input
              type="text"
              class="form-control"
              name="token"
              id="notification-token"
              data-cy="token"
              :class="{ valid: !$v.notification.token.$invalid, invalid: $v.notification.token.$invalid }"
              v-model="$v.notification.token.$model"
              required
            />
            <div v-if="$v.notification.token.$anyDirty && $v.notification.token.$invalid">
              <small class="form-text text-danger" v-if="!$v.notification.token.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notification.token.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 164 })"
              >
                This field cannot be longer than 164 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notification.subject')" for="notification-subject"
              >Subject</label
            >
            <input
              type="text"
              class="form-control"
              name="subject"
              id="notification-subject"
              data-cy="subject"
              :class="{ valid: !$v.notification.subject.$invalid, invalid: $v.notification.subject.$invalid }"
              v-model="$v.notification.subject.$model"
              required
            />
            <div v-if="$v.notification.subject.$anyDirty && $v.notification.subject.$invalid">
              <small class="form-text text-danger" v-if="!$v.notification.subject.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notification.subject.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notification.content')" for="notification-content"
              >Content</label
            >
            <textarea
              class="form-control"
              name="content"
              id="notification-content"
              data-cy="content"
              :class="{ valid: !$v.notification.content.$invalid, invalid: $v.notification.content.$invalid }"
              v-model="$v.notification.content.$model"
              required
            ></textarea>
            <div v-if="$v.notification.content.$anyDirty && $v.notification.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.notification.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notification.content.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 4000 })"
              >
                This field cannot be longer than 4000 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notification.image')" for="notification-image">Image</label>
            <input
              type="text"
              class="form-control"
              name="image"
              id="notification-image"
              data-cy="image"
              :class="{ valid: !$v.notification.image.$invalid, invalid: $v.notification.image.$invalid }"
              v-model="$v.notification.image.$model"
            />
            <div v-if="$v.notification.image.$anyDirty && $v.notification.image.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.notification.image.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 256 })"
              >
                This field cannot be longer than 256 characters.
              </small>
            </div>
          </div>
        </div>
        <div>Data:</div>
        <div class="row justify-content-center">
          <div class="col-sm-12 col-md-5">
            <div class="form-group">
              <label for="dataKey">Key</label>
              <input type="text" class="form-control" id="dataKey" placeholder="Enter key" v-model="data.key" />
            </div>
          </div>
          <div class="col-sm-12 col-md-5 align-self-end">
            <div class="form-group">
              <label for="dataValue">Value</label>
              <input type="text" class="form-control" id="dataValue" placeholder="Enter value" v-model="data.value" />
            </div>
          </div>
          <div class="col-sm-12 col-md-2 align-self-end">
            <div class="form-group">
              <button type="button" id="add-data" class="btn btn-primary" v-on:click="addData()">
                <font-awesome-icon icon="plus"></font-awesome-icon>
              </button>
              <button type="button" id="reset-data" class="btn btn-secondary" v-on:click="resetData()">
                <font-awesome-icon icon="sync"></font-awesome-icon>
              </button>
            </div>
          </div>
        </div>
        <div>
          <div class="alert alert-warning" v-if="!notification.data || notification.data.length === 0">
            <span v-text="$t('messageCentralApp.notificationData.home.notFound')">No notificationData found</span>
          </div>
          <div class="table-responsive" v-if="notification.data && notification.data.length > 0">
            <table class="table table-striped" aria-describedby="notificationData">
              <thead>
                <tr>
                  <th scope="row"><span v-text="$t('messageCentralApp.notificationData.key')">Key</span></th>
                  <th scope="row"><span v-text="$t('messageCentralApp.notificationData.value')">Value</span></th>
                  <th scope="row"></th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="notificationData in notification.data" :key="notificationData.id" data-cy="entityTable">
                  <td>{{ notificationData.key }}</td>
                  <td>{{ notificationData.value }}</td>
                  <td class="text-right">
                    <div class="btn-group">
                      <router-link
                        :to="{ name: 'NotificationDataView', params: { notificationDataId: notificationData.id } }"
                        custom
                        v-slot="{ navigate }"
                      >
                        <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                          <font-awesome-icon icon="eye"></font-awesome-icon>
                          <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                        </button>
                      </router-link>
                      <router-link
                        :to="{ name: 'NotificationDataEdit', params: { notificationDataId: notificationData.id } }"
                        custom
                        v-slot="{ navigate }"
                      >
                        <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                          <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                          <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                        </button>
                      </router-link>
                      <b-button
                        v-on:click="prepareRemove(notificationData)"
                        variant="danger"
                        class="btn btn-sm"
                        data-cy="entityDeleteButton"
                        v-b-modal.removeEntity
                      >
                        <font-awesome-icon icon="times"></font-awesome-icon>
                        <span class="d-none d-md-inline" v-text="$t('entity.action.delete')">Delete</span>
                      </b-button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.notification.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./notification-update.component.ts"></script>

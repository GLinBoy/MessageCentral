<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notifications.home.createLabel"
          data-cy="NotificationCreateHeading"
          v-text="$t('messageCentralApp.notifications.home.createLabel')"
        >
          Create or edit a Notification
        </h2>

        <div class="card">
          <div class="card-header">Notification Receivers:</div>
          <div class="card-body">
            <div class="row justify-content-center">
              <div class="col-sm-12 col-md-5">
                <div class="form-group">
                  <label for="dataKey">Username</label>
                  <input type="text" class="form-control" id="dataKey" placeholder="Enter Username" v-model="receiver.username" />
                </div>
              </div>
              <div class="col-sm-12 col-md-5 align-self-end">
                <div class="form-group">
                  <label for="dataValue">Token</label>
                  <input type="text" class="form-control" id="dataValue" placeholder="Enter Token" v-model="receiver.token" />
                </div>
              </div>
              <div class="col-sm-12 col-md-2 align-self-end">
                <div class="form-group">
                  <button
                    type="button"
                    id="add-data"
                    class="btn btn-primary"
                    v-on:click="addReceiver()"
                    :disabled="!receiver.username && !receiver.token"
                  >
                    <font-awesome-icon icon="plus"></font-awesome-icon>
                  </button>
                  <button
                    type="button"
                    id="reset-data"
                    class="btn btn-secondary"
                    v-on:click="resetReceiver()"
                    :disabled="!receiver.username && !receiver.token"
                  >
                    <font-awesome-icon icon="sync"></font-awesome-icon>
                  </button>
                </div>
              </div>
            </div>
            <div>
              <div class="alert alert-warning" v-if="!notifications.receivers || notifications.receivers.length === 0">
                <span>No Receiver(s) found</span>
              </div>
              <div class="table-responsive card" v-if="notifications.receivers && notifications.receivers.length > 0">
                <table class="table table-striped" aria-describedby="notificationData">
                  <thead>
                    <tr>
                      <th scope="row"><span>username</span></th>
                      <th scope="row"><span>token</span></th>
                      <th scope="row"></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="r in notifications.receivers" :key="r.username" data-cy="entityTable">
                      <td>{{ r.username }}</td>
                      <td>{{ r.token }}</td>
                      <td class="text-right">
                        <div class="btn-group">
                          <b-button
                            v-on:click="prepareReceiverEdit(r)"
                            variant="primary"
                            class="btn btn-sm"
                            data-cy="entityEditButton"
                            v-b-tooltip.hover
                            :title="$t('entity.action.edit')"
                          >
                            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                          </b-button>
                          <b-button
                            v-on:click="prepareReceiverRemove(r)"
                            variant="danger"
                            class="btn btn-sm"
                            data-cy="entityDeleteButton"
                            v-b-modal.removeEntity
                            v-b-tooltip.hover
                            :title="$t('entity.action.delete')"
                          >
                            <font-awesome-icon icon="trash"></font-awesome-icon>
                          </b-button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notifications.subject')" for="notification-subject"
              >Subject</label
            >
            <input
              type="text"
              class="form-control"
              name="subject"
              id="notification-subject"
              data-cy="subject"
              :class="{ valid: !$v.notifications.subject.$invalid, invalid: $v.notifications.subject.$invalid }"
              v-model="$v.notifications.subject.$model"
              required
            />
            <div v-if="$v.notifications.subject.$anyDirty && $v.notifications.subject.$invalid">
              <small class="form-text text-danger" v-if="!$v.notifications.subject.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notifications.subject.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 128 })"
              >
                This field cannot be longer than 128 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notifications.content')" for="notification-content"
              >Content</label
            >
            <textarea
              class="form-control"
              name="content"
              id="notification-content"
              data-cy="content"
              :class="{ valid: !$v.notifications.content.$invalid, invalid: $v.notifications.content.$invalid }"
              v-model="$v.notifications.content.$model"
              required
            ></textarea>
            <div v-if="$v.notifications.content.$anyDirty && $v.notifications.content.$invalid">
              <small class="form-text text-danger" v-if="!$v.notifications.content.required" v-text="$t('entity.validation.required')">
                This field is required.
              </small>
              <small
                class="form-text text-danger"
                v-if="!$v.notifications.content.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 4000 })"
              >
                This field cannot be longer than 4000 characters.
              </small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="$t('messageCentralApp.notifications.image')" for="notification-image">Image</label>
            <input
              type="text"
              class="form-control"
              name="image"
              id="notification-image"
              data-cy="image"
              :class="{ valid: !$v.notifications.image.$invalid, invalid: $v.notifications.image.$invalid }"
              v-model="$v.notifications.image.$model"
            />
            <div v-if="$v.notifications.image.$anyDirty && $v.notifications.image.$invalid">
              <small
                class="form-text text-danger"
                v-if="!$v.notifications.image.maxLength"
                v-text="$t('entity.validation.maxlength', { max: 256 })"
              >
                This field cannot be longer than 256 characters.
              </small>
            </div>
          </div>
        </div>
        <div class="card">
          <div class="card-header">Notification Data:</div>
          <div class="card-body">
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
                  <button type="button" id="add-data" class="btn btn-primary" v-on:click="addData()" :disabled="!data.key">
                    <font-awesome-icon icon="plus"></font-awesome-icon>
                  </button>
                  <button type="button" id="reset-data" class="btn btn-secondary" v-on:click="resetData()" :disabled="!data.key">
                    <font-awesome-icon icon="sync"></font-awesome-icon>
                  </button>
                </div>
              </div>
            </div>
            <div>
              <div class="alert alert-warning" v-if="!notifications.data || notifications.data.length === 0">
                <span v-text="$t('messageCentralApp.notificationData.home.notFound')">No notificationData found</span>
              </div>
              <div class="table-responsive card" v-if="notifications.data && notifications.data.length > 0">
                <table class="table table-striped" aria-describedby="notificationData">
                  <thead>
                    <tr>
                      <th scope="row"><span v-text="$t('messageCentralApp.notificationData.key')">Key</span></th>
                      <th scope="row"><span v-text="$t('messageCentralApp.notificationData.value')">Value</span></th>
                      <th scope="row"></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="notificationData in notifications.data" :key="notificationData.key" data-cy="entityTable">
                      <td>{{ notificationData.key }}</td>
                      <td>{{ notificationData.value }}</td>
                      <td class="text-right">
                        <div class="btn-group">
                          <b-button
                            v-on:click="prepareDataEdit(notificationData)"
                            variant="primary"
                            class="btn btn-sm"
                            data-cy="entityEditButton"
                            v-b-tooltip.hover
                            :title="$t('entity.action.edit')"
                          >
                            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                          </b-button>
                          <b-button
                            v-on:click="prepareDataRemove(notificationData)"
                            variant="danger"
                            class="btn btn-sm"
                            data-cy="entityDeleteButton"
                            v-b-modal.removeEntity
                            v-b-tooltip.hover
                            :title="$t('entity.action.delete')"
                          >
                            <font-awesome-icon icon="trash"></font-awesome-icon>
                          </b-button>
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <br />
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.cancel')">Cancel</span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="$v.notifications.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.save')">Save</span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./notification-multiple.component.ts"></script>

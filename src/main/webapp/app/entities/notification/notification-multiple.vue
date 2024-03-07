<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" role="form" novalidate v-on:submit.prevent="save()">
        <h2
          id="messageCentralApp.notifications.home.createLabel"
          data-cy="NotificationsCreateHeading"
          v-text="t$('messageCentralApp.notifications.home.createLabel')"
        ></h2>
        <div class="card">
          <div class="card-header" v-text="t$('messageCentralApp.notifications.notificationReceiversTitle')" />
          <div class="card-body">
            <div class="row justify-content-center">
              <div class="col-sm-12 col-md-5">
                <div class="form-group">
                  <label for="username" v-text="t$('messageCentralApp.notification.username')" />
                  <input
                    type="text"
                    class="form-control"
                    id="receiver-username"
                    :placeholder="t$('messageCentralApp.notifications.placeholder.enterUsername')"
                    v-model="receiver.username"
                  />
                </div>
              </div>
              <div class="col-sm-12 col-md-5 align-self-end">
                <div class="form-group">
                  <label for="receiver-token" v-text="t$('messageCentralApp.notification.token')" />
                  <input
                    type="text"
                    class="form-control"
                    id="token"
                    :placeholder="t$('messageCentralApp.notifications.placeholder.enterToken')"
                    v-model="receiver.token"
                  />
                </div>
              </div>
              <div class="col-sm-12 col-md-2 align-self-end">
                <div class="form-group">
                  <button
                    type="button"
                    id="add-receiver"
                    class="btn btn-primary"
                    v-on:click="addReceiver()"
                    :disabled="!receiver.username && !receiver.token"
                  >
                    <font-awesome-icon icon="plus"></font-awesome-icon>
                  </button>
                  <button
                    type="button"
                    id="reset-receiver"
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
                <span v-text="t$('messageCentralApp.notifications.messages.noReceiversAdded')" />
              </div>
              <div class="table-responsive card" v-if="notifications.receivers && notifications.receivers.length > 0">
                <table class="table table-striped notification-receiver-table" aria-describedby="notificationData">
                  <thead>
                    <tr>
                      <th scope="row"><span v-text="t$('messageCentralApp.notification.username')" /></th>
                      <th scope="row"><span v-text="t$('messageCentralApp.notification.token')" /></th>
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
                            :title="t$('entity.action.edit')"
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
                            :title="t$('entity.action.delete')"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.subject')" for="notification-subject"></label>
            <input
              type="text"
              class="form-control"
              name="subject"
              id="notification-subject"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.content')" for="notification-content"></label>
            <textarea
              class="form-control"
              name="content"
              id="notification-content"
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
            <label class="form-control-label" v-text="t$('messageCentralApp.notification.image')" for="notification-image"></label>
            <input
              type="text"
              class="form-control"
              name="image"
              id="notification-image"
              data-cy="image"
              :class="{ valid: !v$.image.$invalid, invalid: v$.image.$invalid }"
              v-model="v$.image.$model"
            />
            <div v-if="v$.image.$anyDirty && v$.image.$invalid">
              <small class="form-text text-danger" v-for="error of v$.image.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="card">
            <div class="card-header" v-text="t$('messageCentralApp.notifications.notificationDataTitle')" />
            <div class="card-body">
              <div class="row justify-content-center">
                <div class="col-sm-12 col-md-5">
                  <div class="form-group">
                    <label for="dataKey" v-text="t$('messageCentralApp.notificationData.dataKey')" />
                    <input type="text" class="form-control" id="dataKey" placeholder="Enter key" v-model="notificationData.dataKey" />
                  </div>
                </div>
                <div class="col-sm-12 col-md-5 align-self-end">
                  <div class="form-group">
                    <label for="dataValue" v-text="t$('messageCentralApp.notificationData.dataValue')" />
                    <input type="text" class="form-control" id="dataValue" placeholder="Enter value" v-model="notificationData.dataValue" />
                  </div>
                </div>
                <div class="col-sm-12 col-md-2 align-self-end">
                  <div class="form-group">
                    <button
                      type="button"
                      id="add-data"
                      class="btn btn-primary"
                      v-on:click="addNotificationData()"
                      :disabled="!notificationData.dataKey"
                    >
                      <font-awesome-icon icon="plus"></font-awesome-icon>
                    </button>
                    <button
                      type="button"
                      id="reset-data"
                      class="btn btn-secondary"
                      v-on:click="resetNotificationData()"
                      :disabled="!notificationData.dataKey"
                    >
                      <font-awesome-icon icon="sync"></font-awesome-icon>
                    </button>
                  </div>
                </div>
              </div>
              <div>
                <div class="alert alert-warning" v-if="!v$.data.$model || v$.data.$model.length === 0">
                  <span v-text="t$('messageCentralApp.notificationData.home.notFound')" />
                </div>
                <div class="table-responsive card" v-if="v$.data.$model && v$.data.$model.length > 0">
                  <table class="table table-striped notificatinpm starton-data-table" aria-describedby="notificationData">
                    <thead>
                      <tr>
                        <th scope="row"><span v-text="t$('messageCentralApp.notificationData.dataKey')" /></th>
                        <th scope="row"><span v-text="t$('messageCentralApp.notificationData.dataValue')" /></th>
                        <th scope="row"></th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="nd in v$.data.$model" :key="nd.key" data-cy="entityTable">
                        <td>{{ nd.dataKey }}</td>
                        <td>{{ nd.dataValue }}</td>
                        <td class="text-right">
                          <div class="btn-group">
                            <b-button
                              v-on:click="prepareNotificationDataEdit(nd)"
                              variant="primary"
                              class="btn btn-sm"
                              data-cy="entityEditButton"
                              v-b-tooltip.hover
                              :title="t$('entity.action.edit')"
                            >
                              <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                            </b-button>
                            <b-button
                              v-on:click="prepareNotificationDataRemove(nd)"
                              variant="danger"
                              class="btn btn-sm"
                              data-cy="entityDeleteButton"
                              v-b-modal.removeEntity
                              v-b-tooltip.hover
                              :title="t$('entity.action.delete')"
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
        </div>
        <br />
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
<script lang="ts" src="./notification-multiple.component.ts"></script>
<style lang="scss" scoped src="./notification-multiple.style.scss"></style>

<template>
  <div>
    <h2 id="page-heading" data-cy="NotificationDataHeading">
      <span v-text="$t('messageCentralApp.notificationData.home.title')" id="notification-data-heading">Notification Data</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('messageCentralApp.notificationData.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'NotificationDataCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-notification-data"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('messageCentralApp.notificationData.home.createLabel')"> Create a new Notification Data </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && notificationData && notificationData.length === 0">
      <span v-text="$t('messageCentralApp.notificationData.home.notFound')">No notificationData found</span>
    </div>
    <div class="table-responsive" v-if="notificationData && notificationData.length > 0">
      <table class="table table-striped" aria-describedby="notificationData">
        <thead>
          <tr>
            <th scope="row"><span v-text="$t('global.field.id')">ID</span></th>
            <th scope="row"><span v-text="$t('messageCentralApp.notificationData.key')">Key</span></th>
            <th scope="row"><span v-text="$t('messageCentralApp.notificationData.value')">Value</span></th>
            <th scope="row"><span v-text="$t('messageCentralApp.notificationData.notification')">Notification</span></th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notificationData in notificationData" :key="notificationData.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'NotificationDataView', params: { notificationDataId: notificationData.id } }">{{
                notificationData.id
              }}</router-link>
            </td>
            <td>{{ notificationData.key }}</td>
            <td>{{ notificationData.value }}</td>
            <td>
              <div v-if="notificationData.notification">
                <router-link :to="{ name: 'NotificationView', params: { notificationId: notificationData.notification.id } }">{{
                  notificationData.notification.id
                }}</router-link>
              </div>
            </td>
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
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span
          id="messageCentralApp.notificationData.delete.question"
          data-cy="notificationDataDeleteDialogHeading"
          v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-notificationData-heading" v-text="$t('messageCentralApp.notificationData.delete.question', { id: removeId })">
          Are you sure you want to delete this Notification Data?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-notificationData"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeNotificationData()"
        >
          Delete
        </button>
      </div>
    </b-modal>
  </div>
</template>

<script lang="ts" src="./notification-data.component.ts"></script>

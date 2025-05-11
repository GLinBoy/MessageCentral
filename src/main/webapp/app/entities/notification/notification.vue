<template>
  <div>
    <h2 id="page-heading" data-cy="NotificationHeading">
      <span v-text="t$('messageCentralApp.notification.home.title')" id="notification-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('messageCentralApp.notification.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'NotificationCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-notification"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('messageCentralApp.notification.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && notifications && notifications.length === 0">
      <span v-text="t$('messageCentralApp.notification.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="notifications && notifications.length > 0">
      <table class="table table-striped" aria-describedby="notifications">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('username')">
              <span v-text="t$('messageCentralApp.notification.username')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'username'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('token')">
              <span v-text="t$('messageCentralApp.notification.token')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'token'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('subject')">
              <span v-text="t$('messageCentralApp.notification.subject')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subject'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('content')">
              <span v-text="t$('messageCentralApp.notification.content')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('image')">
              <span v-text="t$('messageCentralApp.notification.image')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'image'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('messageCentralApp.notification.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('messageCentralApp.notification.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdBy')">
              <span v-text="t$('messageCentralApp.notification.createdBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="notification in notifications" :key="notification.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'NotificationView', params: { notificationId: notification.id } }">{{
                notification.id
              }}</router-link>
            </td>
            <td>{{ notification.username }}</td>
            <td>{{ notification.token }}</td>
            <td>{{ notification.subject }}</td>
            <td>{{ notification.content }}</td>
            <td>{{ notification.image }}</td>
            <td v-text="t$('messageCentralApp.MessageStatus.' + notification.status)"></td>
            <td>{{ formatDateShort(notification.createdAt) || '' }}</td>
            <td>{{ notification.createdBy }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'NotificationView', params: { notificationId: notification.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'NotificationEdit', params: { notificationId: notification.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(notification)"
                  variant="danger"
                  class="btn btn-sm"
                  data-cy="entityDeleteButton"
                  v-b-modal.removeEntity
                >
                  <font-awesome-icon icon="times"></font-awesome-icon>
                  <span class="d-none d-md-inline" v-text="t$('entity.action.delete')"></span>
                </b-button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span
          id="messageCentralApp.notification.delete.question"
          data-cy="notificationDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-notification-heading" v-text="t$('messageCentralApp.notification.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-notification"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeNotification()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="notifications && notifications.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./notification.component.ts"></script>

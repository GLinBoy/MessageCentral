<template>
  <div>
    <div class="row">
      <div class="col-sm-4">
        <h2 id="page-heading" data-cy="NotificationHeading">
          <span v-text="$t('messageCentralApp.notification.home.title')" id="notification-heading">Notifications</span>
        </h2>
      </div>
      <div class="col-sm-8">
        <div class="d-flex justify-content-end">
          <button
            class="btn btn-info mr-2"
            v-on:click="handleSyncList"
            :disabled="isFetching"
            v-b-tooltip.hover
            :title="$t('messageCentralApp.notification.home.refreshListLabel')"
          >
            <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          </button>
          <router-link :to="{ name: 'NotificationCreate' }" custom v-slot="{ navigate }">
            <button
              @click="navigate"
              id="jh-create-entity"
              data-cy="entityCreateButton"
              class="btn btn-primary jh-create-entity create-notification"
              v-b-tooltip.hover
              :title="$t('messageCentralApp.notification.home.createLabel')"
            >
              <font-awesome-icon icon="plus"></font-awesome-icon>
            </button>
          </router-link>
        </div>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && notifications && notifications.length === 0">
      <span v-text="$t('messageCentralApp.notification.home.notFound')">No notifications found</span>
    </div>
    <div class="table-responsive" v-if="notifications && notifications.length > 0">
      <table class="table table-striped" aria-describedby="notifications">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('username')">
              <span v-text="$t('messageCentralApp.notification.username')">Username</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'username'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('token')">
              <span v-text="$t('messageCentralApp.notification.token')">Token</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'token'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('subject')">
              <span v-text="$t('messageCentralApp.notification.subject')">Subject</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subject'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('content')">
              <span v-text="$t('messageCentralApp.notification.content')">Content</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('image')">
              <span v-text="$t('messageCentralApp.notification.image')">Image</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'image'"></jhi-sort-indicator>
            </th>
          </tr>
        </thead>
        <tbody>
          <router-link
            v-for="notification in notifications"
            :key="notification.id"
            :to="{ name: 'NotificationView', params: { notificationId: notification.id } }"
            tag="tr"
            data-cy="entityTable"
          >
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
          </router-link>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span
          id="messageCentralApp.notification.delete.question"
          data-cy="notificationDeleteDialogHeading"
          v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-notification-heading" v-text="$t('messageCentralApp.notification.delete.question', { id: removeId })">
          Are you sure you want to delete this Notification?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-notification"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeNotification()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="notifications && notifications.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./notification.component.ts"></script>

<style lang="scss" scoped src="./notification.style.scss"></style>

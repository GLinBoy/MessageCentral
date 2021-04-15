<template>
  <div>
    <h2 id="page-heading" data-cy="EmailHeading">
      <span v-text="$t('messageCentralApp.email.home.title')" id="email-heading">Emails</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('messageCentralApp.email.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'EmailCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-email"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('messageCentralApp.email.home.createLabel')"> Create a new Email </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && emails && emails.length === 0">
      <span v-text="$t('messageCentralApp.email.home.notFound')">No emails found</span>
    </div>
    <div class="table-responsive" v-if="emails && emails.length > 0">
      <table class="table table-striped" aria-describedby="emails">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('receiver')">
              <span v-text="$t('messageCentralApp.email.receiver')">Receiver</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'receiver'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('subject')">
              <span v-text="$t('messageCentralApp.email.subject')">Subject</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subject'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('content')">
              <span v-text="$t('messageCentralApp.email.content')">Content</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="email in emails" :key="email.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'EmailView', params: { emailId: email.id } }">{{ email.id }}</router-link>
            </td>
            <td>{{ email.receiver }}</td>
            <td>{{ email.subject }}</td>
            <td>{{ email.content }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'EmailView', params: { emailId: email.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'EmailEdit', params: { emailId: email.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(email)"
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
        ><span id="messageCentralApp.email.delete.question" data-cy="emailDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-email-heading" v-text="$t('messageCentralApp.email.delete.question', { id: removeId })">
          Are you sure you want to delete this Email?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-email"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeEmail()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="emails && emails.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./email.component.ts"></script>

<template>
  <div>
    <h2 id="page-heading" data-cy="EmailHeading">
      <span v-text="t$('messageCentralApp.email.home.title')" id="email-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('messageCentralApp.email.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'EmailCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-email"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('messageCentralApp.email.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && emails && emails.length === 0">
      <span v-text="t$('messageCentralApp.email.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="emails && emails.length > 0">
      <table class="table table-striped" aria-describedby="emails">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('receiver')">
              <span v-text="t$('messageCentralApp.email.receiver')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'receiver'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('subject')">
              <span v-text="t$('messageCentralApp.email.subject')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'subject'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('content')">
              <span v-text="t$('messageCentralApp.email.content')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('messageCentralApp.email.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('emailType')">
              <span v-text="t$('messageCentralApp.email.emailType')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'emailType'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('messageCentralApp.email.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdBy')">
              <span v-text="t$('messageCentralApp.email.createdBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
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
            <td v-text="t$('messageCentralApp.MessageStatus.' + email.status)"></td>
            <td v-text="t$('messageCentralApp.EmailType.' + email.emailType)"></td>
            <td>{{ formatDateShort(email.createdAt) || '' }}</td>
            <td>{{ email.createdBy }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'EmailView', params: { emailId: email.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'EmailEdit', params: { emailId: email.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(email)"
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
        <span id="messageCentralApp.email.delete.question" data-cy="emailDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-email-heading" v-text="t$('messageCentralApp.email.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-email"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeEmail()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="emails && emails.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./email.component.ts"></script>

<template>
  <div>
    <h2 id="page-heading" data-cy="ShortMessageHeading">
      <span v-text="t$('messageCentralApp.shortMessage.home.title')" id="short-message-heading"></span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" @click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="t$('messageCentralApp.shortMessage.home.refreshListLabel')"></span>
        </button>
        <router-link :to="{ name: 'ShortMessageCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-short-message"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="t$('messageCentralApp.shortMessage.home.createLabel')"></span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shortMessages && shortMessages.length === 0">
      <span v-text="t$('messageCentralApp.shortMessage.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="shortMessages && shortMessages.length > 0">
      <table class="table table-striped" aria-describedby="shortMessages">
        <thead>
          <tr>
            <th scope="row" @click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('phoneNumber')">
              <span v-text="t$('messageCentralApp.shortMessage.phoneNumber')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phoneNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('content')">
              <span v-text="t$('messageCentralApp.shortMessage.content')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('status')">
              <span v-text="t$('messageCentralApp.shortMessage.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdAt')">
              <span v-text="t$('messageCentralApp.shortMessage.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" @click="changeOrder('createdBy')">
              <span v-text="t$('messageCentralApp.shortMessage.createdBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="shortMessage in shortMessages" :key="shortMessage.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }">{{
                shortMessage.id
              }}</router-link>
            </td>
            <td>{{ shortMessage.phoneNumber }}</td>
            <td>{{ shortMessage.content }}</td>
            <td v-text="t$('messageCentralApp.MessageStatus.' + shortMessage.status)"></td>
            <td>{{ formatDateShort(shortMessage.createdAt) || '' }}</td>
            <td>{{ shortMessage.createdBy }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.view')"></span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'ShortMessageEdit', params: { shortMessageId: shortMessage.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="t$('entity.action.edit')"></span>
                  </button>
                </router-link>
                <b-button
                  @click="prepareRemove(shortMessage)"
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
          id="messageCentralApp.shortMessage.delete.question"
          data-cy="shortMessageDeleteDialogHeading"
          v-text="t$('entity.delete.title')"
        ></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-shortMessage-heading" v-text="t$('messageCentralApp.shortMessage.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" @click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shortMessage"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            @click="removeShortMessage()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="shortMessages && shortMessages.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./short-message.component.ts"></script>

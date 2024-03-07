<template>
  <div>
    <div class="row">
      <div class="col-xs-12 col-md-4 col-lg-8">
        <h2 id="page-heading" data-cy="ShortMessageHeading">
          <span v-text="t$('messageCentralApp.shortMessage.home.title')" id="short-message-heading"></span>
        </h2>
      </div>
      <div class="col-xs-12 col-md-8 col-lg-4">
        <div class="d-flex justify-content-end">
          <b-input-group class="mr-2">
            <b-form-input
              type="text"
              v-model="currentSearch"
              @keydown.enter.native="handleSearch"
              :placeholder="t$('messageCentralApp.shortMessage.home.searchPlaceholder')"
            />

            <b-input-group-append>
              <b-button variant="outline-primary" v-on:click="handleSearch">
                <font-awesome-icon icon="search"></font-awesome-icon>
              </b-button>
            </b-input-group-append>
          </b-input-group>
          <button
            class="btn btn-info mr-2"
            v-on:click="handleSyncList"
            :disabled="isFetching"
            v-b-tooltip.hover
            :title="$t('messageCentralApp.shortMessage.home.refreshListLabel')"
          >
            <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          </button>
          <router-link :to="{ name: 'ShortMessageCreate' }" custom v-slot="{ navigate }">
            <button
              @click="navigate"
              id="jh-create-entity"
              data-cy="entityCreateButton"
              class="btn btn-primary jh-create-entity create-short-message"
              v-b-tooltip.hover
              :title="$t('messageCentralApp.shortMessage.home.createLabel')"
            >
              <font-awesome-icon icon="plus"></font-awesome-icon>
            </button>
          </router-link>
        </div>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && shortMessages && shortMessages.length === 0">
      <span v-text="t$('messageCentralApp.shortMessage.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="shortMessages && shortMessages.length > 0">
      <table class="table table-striped messages-list-table" aria-describedby="shortMessages">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('phoneNumber')">
              <span v-text="t$('messageCentralApp.shortMessage.phoneNumber')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phoneNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('content')">
              <span v-text="t$('messageCentralApp.shortMessage.content')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdAt')">
              <span v-text="t$('messageCentralApp.shortMessage.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdBy')">
              <span v-text="t$('messageCentralApp.shortMessage.createdBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="t$('messageCentralApp.shortMessage.status')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
          </tr>
        </thead>
        <tbody>
          <router-link
            v-for="shortMessage in shortMessages"
            :key="shortMessage.id"
            :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }"
            custom
            v-slot="{ navigate }"
          >
            <tr data-cy="entityTable" @click="navigate" @keypress.enter="navigate">
              <td>
                <router-link :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }"
                  >{{ shortMessage.id }}
                </router-link>
              </td>
              <td>{{ shortMessage.phoneNumber }}</td>
              <td>{{ shortMessage.content }}</td>
              <td>{{ formatDateShort(shortMessage.createdAt) || '' }}</td>
              <td>{{ shortMessage.createdBy }}</td>
              <td>
                <b-badge :variant="getVariant(shortMessage.status)">
                  {{ shortMessage.status.replace('_', ' ') }}
                </b-badge>
              </td>
            </tr>
          </router-link>
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
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-shortMessage"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeShortMessage()"
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

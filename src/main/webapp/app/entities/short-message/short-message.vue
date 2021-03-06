<template>
  <div>
    <div class="row">
      <div class="col-xs-12 col-md-4 col-lg-8">
        <h2 id="page-heading" data-cy="ShortMessageHeading">
          <span v-text="$t('messageCentralApp.shortMessage.home.title')" id="short-message-heading">Short Messages</span>
        </h2>
      </div>
      <div class="col-xs-12 col-md-8 col-lg-4">
        <div class="d-flex justify-content-end">
          <b-input-group class="mr-2">
            <b-form-input
              type="text"
              v-model="currentSearch"
              @keydown.enter.native="handleSearch"
              :placeholder="$t('messageCentralApp.shortMessage.home.searchPlaceholder')"
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
      <span v-text="$t('messageCentralApp.shortMessage.home.notFound')">No shortMessages found</span>
    </div>
    <div class="table-responsive" v-if="shortMessages && shortMessages.length > 0">
      <table class="table table-striped" aria-describedby="shortMessages">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('phoneNumber')">
              <span v-text="$t('messageCentralApp.shortMessage.phoneNumber')">Phone Number</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'phoneNumber'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('content')">
              <span v-text="$t('messageCentralApp.shortMessage.content')">Content</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'content'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdAt')">
              <span v-text="$t('messageCentralApp.shortMessage.createdAt')">createdAt</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdBy')">
              <span v-text="$t('messageCentralApp.shortMessage.createdBy')">createdBy</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('status')">
              <span v-text="$t('messageCentralApp.shortMessage.status')">Status</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'status'"></jhi-sort-indicator>
            </th>
          </tr>
        </thead>
        <tbody>
          <router-link
            v-for="shortMessage in shortMessages"
            :key="shortMessage.id"
            :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }"
            tag="tr"
            data-cy="entityTable"
          >
            <td>
              <router-link :to="{ name: 'ShortMessageView', params: { shortMessageId: shortMessage.id } }">{{
                shortMessage.id
              }}</router-link>
            </td>
            <td>{{ shortMessage.phoneNumber }}</td>
            <td>{{ shortMessage.content }}</td>
            <td>{{ shortMessage.createdAt ? $d(Date.parse(shortMessage.createdAt), 'short') : '' }}</td>
            <td>{{ shortMessage.createdBy }}</td>
            <td>
              <b-badge :variant="getVariant(shortMessage.status)">
                {{ shortMessage.status.replace('_', ' ') }}
              </b-badge>
            </td>
          </router-link>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <span slot="modal-title"
        ><span
          id="messageCentralApp.shortMessage.delete.question"
          data-cy="shortMessageDeleteDialogHeading"
          v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-shortMessage-heading" v-text="$t('messageCentralApp.shortMessage.delete.question', { id: removeId })">
          Are you sure you want to delete this Short Message?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-shortMessage"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeShortMessage()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="shortMessages && shortMessages.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./short-message.component.ts"></script>

<style lang="scss" scoped src="./short-message.style.scss"></style>

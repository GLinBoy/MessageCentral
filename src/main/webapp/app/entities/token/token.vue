<template>
  <div>
    <h2 id="page-heading" data-cy="TokenHeading">
      <span v-text="$t('messageCentralApp.token.home.title')" id="token-heading">Tokens</span>
      <div class="d-flex justify-content-end">
        <button class="btn btn-info mr-2" v-on:click="handleSyncList" :disabled="isFetching">
          <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          <span v-text="$t('messageCentralApp.token.home.refreshListLabel')">Refresh List</span>
        </button>
        <router-link :to="{ name: 'TokenCreate' }" custom v-slot="{ navigate }">
          <button
            @click="navigate"
            id="jh-create-entity"
            data-cy="entityCreateButton"
            class="btn btn-primary jh-create-entity create-token"
          >
            <font-awesome-icon icon="plus"></font-awesome-icon>
            <span v-text="$t('messageCentralApp.token.home.createLabel')"> Create a new Token </span>
          </button>
        </router-link>
      </div>
    </h2>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && tokens && tokens.length === 0">
      <span v-text="$t('messageCentralApp.token.home.notFound')">No tokens found</span>
    </div>
    <div class="table-responsive" v-if="tokens && tokens.length > 0">
      <table class="table table-striped" aria-describedby="tokens">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="$t('global.field.id')">ID</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="$t('messageCentralApp.token.name')">Name</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('token')">
              <span v-text="$t('messageCentralApp.token.token')">Token</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'token'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('disable')">
              <span v-text="$t('messageCentralApp.token.disable')">Disable</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'disable'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdAt')">
              <span v-text="$t('messageCentralApp.token.createdAt')">Created At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('deprecateAt')">
              <span v-text="$t('messageCentralApp.token.deprecateAt')">Deprecate At</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deprecateAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('roles')">
              <span v-text="$t('messageCentralApp.token.roles')">Roles</span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'roles'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="token in tokens" :key="token.id" data-cy="entityTable">
            <td>
              <router-link :to="{ name: 'TokenView', params: { tokenId: token.id } }">{{ token.id }}</router-link>
            </td>
            <td>{{ token.name }}</td>
            <td>{{ token.token }}</td>
            <td>{{ token.disable }}</td>
            <td>{{ token.createdAt ? $d(Date.parse(token.createdAt), 'short') : '' }}</td>
            <td>{{ token.deprecateAt ? $d(Date.parse(token.deprecateAt), 'short') : '' }}</td>
            <td>{{ token.roles }}</td>
            <td class="text-right">
              <div class="btn-group">
                <router-link :to="{ name: 'TokenView', params: { tokenId: token.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-info btn-sm details" data-cy="entityDetailsButton">
                    <font-awesome-icon icon="eye"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.view')">View</span>
                  </button>
                </router-link>
                <router-link :to="{ name: 'TokenEdit', params: { tokenId: token.id } }" custom v-slot="{ navigate }">
                  <button @click="navigate" class="btn btn-primary btn-sm edit" data-cy="entityEditButton">
                    <font-awesome-icon icon="pencil-alt"></font-awesome-icon>
                    <span class="d-none d-md-inline" v-text="$t('entity.action.edit')">Edit</span>
                  </button>
                </router-link>
                <b-button
                  v-on:click="prepareRemove(token)"
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
        ><span id="messageCentralApp.token.delete.question" data-cy="tokenDeleteDialogHeading" v-text="$t('entity.delete.title')"
          >Confirm delete operation</span
        ></span
      >
      <div class="modal-body">
        <p id="jhi-delete-token-heading" v-text="$t('messageCentralApp.token.delete.question', { id: removeId })">
          Are you sure you want to delete this Token?
        </p>
      </div>
      <div slot="modal-footer">
        <button type="button" class="btn btn-secondary" v-text="$t('entity.action.cancel')" v-on:click="closeDialog()">Cancel</button>
        <button
          type="button"
          class="btn btn-primary"
          id="jhi-confirm-delete-token"
          data-cy="entityConfirmDeleteButton"
          v-text="$t('entity.action.delete')"
          v-on:click="removeToken()"
        >
          Delete
        </button>
      </div>
    </b-modal>
    <div v-show="tokens && tokens.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage" :change="loadPage(page)"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token.component.ts"></script>
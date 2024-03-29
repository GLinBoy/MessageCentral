<template>
  <div>
    <div class="row">
      <div class="col-xs-12 col-md-4 col-lg-8">
        <h2 id="page-heading" data-cy="TokenHeading">
          <span v-text="t$('messageCentralApp.token.home.title')" id="token-heading"></span>
        </h2>
      </div>
      <div class="col-xs-12 col-md-8 col-lg-4">
        <div class="d-flex justify-content-end">
          <b-input-group class="mr-2">
            <b-form-input
              type="text"
              v-model="currentSearch"
              @keydown.enter.native="handleSearch"
              :placeholder="t$('messageCentralApp.token.home.searchPlaceholder')"
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
            :title="$t('messageCentralApp.token.home.refreshListLabel')"
          >
            <font-awesome-icon icon="sync" :spin="isFetching"></font-awesome-icon>
          </button>
          <router-link :to="{ name: 'TokenCreate' }" custom v-slot="{ navigate }">
            <button
              @click="navigate"
              id="jh-create-entity"
              data-cy="entityCreateButton"
              class="btn btn-primary jh-create-entity create-short-message"
              v-b-tooltip.hover
              :title="$t('messageCentralApp.token.home.createLabel')"
            >
              <font-awesome-icon icon="plus"></font-awesome-icon>
            </button>
          </router-link>
        </div>
      </div>
    </div>
    <br />
    <div class="alert alert-warning" v-if="!isFetching && tokens && tokens.length === 0">
      <span v-text="t$('messageCentralApp.token.home.notFound')"></span>
    </div>
    <div class="table-responsive" v-if="tokens && tokens.length > 0">
      <table class="table table-striped messages-list-table" aria-describedby="tokens">
        <thead>
          <tr>
            <th scope="row" v-on:click="changeOrder('id')">
              <span v-text="t$('global.field.id')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'id'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('name')">
              <span v-text="t$('messageCentralApp.token.name')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'name'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('token')">
              <span v-text="t$('messageCentralApp.token.token')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'token'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('disable')">
              <span v-text="t$('messageCentralApp.token.disable')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'disable'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('deprecateAt')">
              <span v-text="t$('messageCentralApp.token.deprecateAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'deprecateAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdAt')">
              <span v-text="t$('messageCentralApp.token.createdAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('createdBy')">
              <span v-text="t$('messageCentralApp.token.createdBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'createdBy'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('updatedAt')">
              <span v-text="t$('messageCentralApp.token.updatedAt')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedAt'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('updatedBy')">
              <span v-text="t$('messageCentralApp.token.updatedBy')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'updatedBy'"></jhi-sort-indicator>
            </th>
            <th scope="row" v-on:click="changeOrder('roles')">
              <span v-text="t$('messageCentralApp.token.roles')"></span>
              <jhi-sort-indicator :current-order="propOrder" :reverse="reverse" :field-name="'roles'"></jhi-sort-indicator>
            </th>
            <th scope="row"></th>
          </tr>
        </thead>
        <tbody>
          <router-link
            v-for="token in tokens"
            :key="token.id"
            :to="{ name: 'TokenView', params: { tokenId: token.id } }"
            custom
            v-slot="{ navigate }"
          >
            <tr data-cy="entityTable" @click="navigate" @keypress.enter="navigate">
              <td>
                <router-link :to="{ name: 'TokenView', params: { tokenId: token.id } }">{{ token.id }}</router-link>
              </td>
              <td>{{ token.name }}</td>
              <td>{{ token.token }}</td>
              <td>
                <b-badge :variant="token.disable ? 'danger' : 'success'">
                  {{ token.disable ? $t('entity.action.disable') : $t('entity.action.enable') }}
                </b-badge>
              </td>
              <td>{{ formatDateShort(token.deprecateAt) || '' }}</td>
              <td>{{ formatDateShort(token.createdAt) || '' }}</td>
              <td>{{ token.createdBy }}</td>
              <td>{{ formatDateShort(token.updatedAt) || '' }}</td>
              <td>{{ token.updatedBy }}</td>
              <td>
                <font-awesome-icon
                  icon="at"
                  :style="[1, 3, 5, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
                />
                <font-awesome-icon
                  icon="sms"
                  :style="[2, 3, 6, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
                />
                <font-awesome-icon
                  icon="bell"
                  :style="[4, 5, 6, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
                />
              </td>
              <td class="text-right" @click.stop>
                <div class="btn-group">
                  <b-button
                    v-show="token.disable"
                    v-on:click="enableToken(token)"
                    variant="info"
                    class="btn btn-sm"
                    data-cy="entityEnableButton"
                    v-b-tooltip.hover
                    :title="$t('entity.action.enable')"
                  >
                    <font-awesome-icon icon="toggle-off"></font-awesome-icon>
                  </b-button>
                  <b-button
                    v-show="!token.disable"
                    v-on:click="disableToken(token)"
                    variant="info"
                    class="btn btn-sm"
                    data-cy="entityDisableButton"
                    v-b-tooltip.hover
                    :title="$t('entity.action.disable')"
                  >
                    <font-awesome-icon icon="toggle-on"></font-awesome-icon>
                  </b-button>
                  <b-button
                    v-on:click="prepareRemove(token)"
                    variant="danger"
                    class="btn btn-sm"
                    data-cy="entityDeleteButton"
                    v-b-modal.removeEntity
                    v-b-tooltip.hover
                    :title="$t('entity.action.delete')"
                  >
                    <font-awesome-icon icon="trash"></font-awesome-icon>
                  </b-button>
                </div>
              </td>
            </tr>
          </router-link>
        </tbody>
      </table>
    </div>
    <b-modal ref="removeEntity" id="removeEntity">
      <template #modal-title>
        <span id="messageCentralApp.token.delete.question" data-cy="tokenDeleteDialogHeading" v-text="t$('entity.delete.title')"></span>
      </template>
      <div class="modal-body">
        <p id="jhi-delete-token-heading" v-text="t$('messageCentralApp.token.delete.question', { id: removeId })"></p>
      </div>
      <template #modal-footer>
        <div>
          <button type="button" class="btn btn-secondary" v-text="t$('entity.action.cancel')" v-on:click="closeDialog()"></button>
          <button
            type="button"
            class="btn btn-primary"
            id="jhi-confirm-delete-token"
            data-cy="entityConfirmDeleteButton"
            v-text="t$('entity.action.delete')"
            v-on:click="removeToken()"
          ></button>
        </div>
      </template>
    </b-modal>
    <div v-show="tokens && tokens.length > 0">
      <div class="row justify-content-center">
        <jhi-item-count :page="page" :total="queryCount" :itemsPerPage="itemsPerPage"></jhi-item-count>
      </div>
      <div class="row justify-content-center">
        <b-pagination size="md" :total-rows="totalItems" v-model="page" :per-page="itemsPerPage"></b-pagination>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token.component.ts"></script>

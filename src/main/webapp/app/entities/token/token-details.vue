<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="token">
        <h2 class="jh-entity-heading" data-cy="tokenDetailsHeading">
          <span v-text="$t('messageCentralApp.token.detail.title')">Token</span> {{ token.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('messageCentralApp.token.name')">Name</span>
          </dt>
          <dd>
            <span>{{ token.name }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.token')">Token</span>
          </dt>
          <dd>
            <span>{{ token.token }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.disable')">Disable</span>
          </dt>
          <dd>
            <span>{{ token.disable }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.deprecateAt')">Deprecate At</span>
          </dt>
          <dd>
            <span v-if="token.deprecateAt">{{ $d(Date.parse(token.deprecateAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.roles')">Roles</span>
          </dt>
          <dd>
            <span>{{ token.roles }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.createdAt')">Created At</span>
          </dt>
          <dd>
            <span v-if="token.createdAt">{{ $d(Date.parse(token.createdAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.createdBy')">Created By</span>
          </dt>
          <dd>
            <span>{{ token.createdBy }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.updatedAt')">Updated At</span>
          </dt>
          <dd>
            <span v-if="token.updatedAt">{{ $d(Date.parse(token.updatedAt), 'long') }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.token.updatedBy')">Updated By</span>
          </dt>
          <dd>
            <span>{{ token.updatedBy }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link v-if="token.id" :to="{ name: 'TokenEdit', params: { tokenId: token.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token-details.component.ts"></script>

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
            <span v-text="$t('messageCentralApp.token.createdAt')">Created At</span>
          </dt>
          <dd>
            <span v-if="token.createdAt">{{ $d(Date.parse(token.createdAt), 'long') }}</span>
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
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token-details.component.ts"></script>

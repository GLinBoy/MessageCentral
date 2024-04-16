<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="token">
        <h2 class="jh-entity-heading" data-cy="tokenDetailsHeading">
          <span v-text="t$('messageCentralApp.token.detail.title')"></span> {{ token.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('messageCentralApp.token.name')"></span>
          </dt>
          <dd>
            <span>{{ token.name }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.token')"></span>
          </dt>
          <dd>
            <span>{{ token.token }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.disable')"></span>
          </dt>
          <dd>
            <span>{{ token.disable }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.deprecateAt')"></span>
          </dt>
          <dd>
            <span v-if="token.deprecateAt">{{ formatDateLong(token.deprecateAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.roles')"></span>
          </dt>
          <dd>
            <span>{{ token.roles }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="token.createdAt">{{ formatDateLong(token.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.createdBy')"></span>
          </dt>
          <dd>
            <span>{{ token.createdBy }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="token.updatedAt">{{ formatDateLong(token.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.updatedBy')"></span>
          </dt>
          <dd>
            <span>{{ token.updatedBy }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <router-link v-if="token.id" :to="{ name: 'TokenEdit', params: { tokenId: token.id } }" custom v-slot="{ navigate }">
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.edit')"></span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token-details.component.ts"></script>

<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="shortMessage">
        <h2 class="jh-entity-heading" data-cy="shortMessageDetailsHeading">
          <span v-text="$t('messageCentralApp.shortMessage.detail.title')">ShortMessage</span> {{ shortMessage.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('messageCentralApp.shortMessage.phoneNumber')">Phone Number</span>
          </dt>
          <dd>
            <span>{{ shortMessage.phoneNumber }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.shortMessage.content')">Content</span>
          </dt>
          <dd>
            <span>{{ shortMessage.content }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="shortMessage.id"
          :to="{ name: 'ShortMessageEdit', params: { shortMessageId: shortMessage.id } }"
          custom
          v-slot="{ navigate }"
        >
          <button @click="navigate" class="btn btn-primary">
            <font-awesome-icon icon="pencil-alt"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.edit')"> Edit</span>
          </button>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./short-message-details.component.ts"></script>

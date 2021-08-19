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
          <dt>
            <span v-text="$t('messageCentralApp.shortMessage.status')">Status</span>
          </dt>
          <dd>
            <b-badge :variant="getVariant(shortMessage.status)">
              {{ shortMessage && shortMessage.status ? shortMessage.status.replace('_', ' ') : '' }}
            </b-badge>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./short-message-details.component.ts"></script>

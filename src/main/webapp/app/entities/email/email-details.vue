<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="email">
        <h2 class="jh-entity-heading" data-cy="emailDetailsHeading">
          <span v-text="$t('messageCentralApp.email.detail.title')">Email</span> {{ email.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('messageCentralApp.email.receiver')">Receiver</span>
          </dt>
          <dd>
            <span>{{ email.receiver }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.email.subject')">Subject</span>
          </dt>
          <dd>
            <span>{{ email.subject }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.email.content')">Content</span>
          </dt>
          <dd>
            <span>{{ email.content }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.email.status')">Status</span>
          </dt>
          <dd>
            <b-badge :variant="getVariant(email.status)">
              {{ email && email.status ? email.status.replace('_', ' ') : '' }}
            </b-badge>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.email.createdAt')">Created At</span>
          </dt>
          <dd>
            <span>{{ email.createdAt }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.email.createdBy')">Created By</span>
          </dt>
          <dd>
            <span>{{ email.createdBy }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./email-details.component.ts"></script>

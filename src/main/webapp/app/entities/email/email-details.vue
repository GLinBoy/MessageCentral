<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="email">
        <h2 class="jh-entity-heading" data-cy="emailDetailsHeading">
          <span v-text="t$('messageCentralApp.email.detail.title')"></span> {{ email.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('messageCentralApp.email.receiver')"></span>
          </dt>
          <dd>
            <span>{{ email.receiver }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.subject')"></span>
          </dt>
          <dd>
            <span>{{ email.subject }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.content')"></span>
          </dt>
          <dd>
            <span>{{ email.content }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.emailType')"></span>
          </dt>
          <dd>
            <b-badge variant="info">
              {{ email.emailType }}
            </b-badge>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.status')"></span>
          </dt>
          <dd>
            <b-badge :variant="getVariant(email.status)">
              {{ email && email.status ? email.status.replace('_', ' ') : '' }}
            </b-badge>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="email.createdAt">{{ formatDateLong(email.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.email.createdBy')"></span>
          </dt>
          <dd>
            <span>{{ email.createdBy }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./email-details.component.ts"></script>

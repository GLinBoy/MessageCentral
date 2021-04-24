<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="notification">
        <h2 class="jh-entity-heading" data-cy="notificationDetailsHeading">
          <span v-text="$t('messageCentralApp.notification.detail.title')">Notification</span> {{ notification.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="$t('messageCentralApp.notification.username')">Username</span>
          </dt>
          <dd>
            <span>{{ notification.username }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.token')">Token</span>
          </dt>
          <dd>
            <span>{{ notification.token }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.subject')">Subject</span>
          </dt>
          <dd>
            <span>{{ notification.subject }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.content')">Content</span>
          </dt>
          <dd>
            <span>{{ notification.content }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.image')">Image</span>
          </dt>
          <dd>
            <span>{{ notification.image }}</span>
          </dd>
        </dl>
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
        <router-link
          v-if="notification.id"
          :to="{ name: 'NotificationEdit', params: { notificationId: notification.id } }"
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

<script lang="ts" src="./notification-details.component.ts"></script>

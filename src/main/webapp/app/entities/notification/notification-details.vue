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
          <dt>
            <span v-text="$t('messageCentralApp.notification.status')">Status</span>
          </dt>
          <dd>
            <b-badge :variant="getVariant(notification.status)">
              {{ notification && notification.status ? notification.status.replace('_', ' ') : '' }}
            </b-badge>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.createdAt')">Created At</span>
          </dt>
          <dd>
            <span>{{ notification.createdAt ? $d(Date.parse(notification.createdAt), 'short') : '' }}</span>
          </dd>
          <dt>
            <span v-text="$t('messageCentralApp.notification.createdBy')">Created By</span>
          </dt>
          <dd>
            <span>{{ notification.createdBy }}</span>
          </dd>
        </dl>
        <div class="card">
          <div class="card-header" v-text="$t('messageCentralApp.notifications.notificationDataTitle')">Notification Data:</div>
          <div class="card-body">
            <div>
              <div class="alert alert-warning" v-if="!notification.data || notification.data.length === 0">
                <span v-text="$t('messageCentralApp.notificationData.home.notFound')">No notificationData found</span>
              </div>
              <div class="table-responsive card" v-if="notification.data && notification.data.length > 0">
                <table class="table table-striped" aria-describedby="notificationData">
                  <thead>
                    <tr>
                      <th scope="row"><span v-text="$t('messageCentralApp.notificationData.dataKey')">Key</span></th>
                      <th scope="row"><span v-text="$t('messageCentralApp.notificationData.dataValue')">Value</span></th>
                    </tr>
                  </thead>
                  <tbody>
                    <tr v-for="notificationData in notification.data" :key="notificationData.dataKey" data-cy="entityTable">
                      <td>{{ notificationData.dataKey }}</td>
                      <td>{{ notificationData.dataValue }}</td>
                    </tr>
                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>
        <br />
        <button type="submit" v-on:click.prevent="previousState()" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="$t('entity.action.back')"> Back</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./notification-details.component.ts"></script>

<style lang="scss" scoped src="./notification-details.style.scss"></style>

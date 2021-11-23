<template>
  <div>
    <b-dropdown-item to="/email">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.email')">Email</span>
    </b-dropdown-item>
    <b-dropdown-item to="/short-message">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.shortMessage')">Short Message</span>
    </b-dropdown-item>
    <b-dropdown-item to="/notification">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.notification')">Notification</span>
    </b-dropdown-item>
    <b-dropdown-item to="/notification-data">
      <font-awesome-icon icon="asterisk" />
      <span v-text="$t('global.menu.entities.notificationData')">Notification Data</span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

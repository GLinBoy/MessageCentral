<template>
  <div>
    <b-dropdown-item to="/email">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.email')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/short-message">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.shortMessage')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/notification">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.notification')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/notification-data">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.notificationData')"></span>
    </b-dropdown-item>
    <b-dropdown-item to="/token">
      <font-awesome-icon icon="asterisk" />
      <span v-text="t$('global.menu.entities.token')"></span>
    </b-dropdown-item>
    <!-- jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here -->
  </div>
</template>

<script lang="ts" src="./entities-menu.component.ts"></script>

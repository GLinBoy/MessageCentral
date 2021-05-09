<template>
  <div class="home row">
    <div class="col-md-3">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <login-form v-if="!authenticated" />
    </div>
  </div>
</template>

<script lang="ts" src="./home.component.ts"></script>

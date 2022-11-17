<template>
  <div id="footer" class="footer row" style="padding-top: 5px; margin-top: 5px">
    <div class="col-md-6">
      <h5 v-text="$t('footer[\'message-central.title.link\']')">MessageCentral Links:</h5>
      <ul>
        <li v-for="link in mcLinks">
          <a :href="link.href" :target="link.target" :rel="link.rel" v-text="$t(link.vtext, { app_name: 'MessageCentral' })">
            {{ link.text }}
          </a>
        </li>
      </ul>

      <p>
        <span v-text="$t('home.like')">If you like this application, don't forget to give us a star on</span>
        <a href="https://github.com/GLinBoy/MessageCentral" target="_blank" rel="noopener noreferrer" v-text="$t('home.github')"> GitHub </a
        >!
      </p>
    </div>
    <div class="col-md-6">
      <h5 v-text="$t('footer[\'jhipster.title.link\']')">JHipster Links:</h5>
      <ul>
        <li v-for="link in jhipsterLinks">
          <a :href="link.href" :target="link.target" :rel="link.rel" v-text="$t(link.vtext, { app_name: 'Jhipster' })">
            {{ link.text }}
          </a>
        </li>
      </ul>

      <p>
        <span v-text="$t('home.like')">If you like JHipster, don't forget to give us a star on</span>
        <a href="https://github.com/jhipster/generator-jhipster" target="_blank" rel="noopener noreferrer" v-text="$t('home.github')">
          GitHub </a
        >!
      </p>
    </div>
  </div>
</template>

<script lang="ts" src="./jhi-footer.component.ts"></script>

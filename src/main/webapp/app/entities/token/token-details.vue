<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <div v-if="token">
        <h2 class="jh-entity-heading" data-cy="tokenDetailsHeading">
          <span v-text="t$('messageCentralApp.token.detail.title')"></span> {{ token.id }}
        </h2>
        <dl class="row jh-entity-details">
          <dt>
            <span v-text="t$('messageCentralApp.token.name')"></span>
          </dt>
          <dd>
            <span>{{ token.name }}&nbsp;</span>
            <b-badge :variant="token.disable ? 'danger' : 'success'">
              {{ token.disable ? $t('entity.action.disable') : $t('entity.action.enable') }}
            </b-badge>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.token')"></span>
          </dt>
          <dd>
            <div class="input-group">
              <b-form-input v-on:focus="$event.target.select()" ref="clone" readonly :value="token.token" />
              <b-button variant="dark" squared size="sm" @click="copy" v-b-tooltip.hover title="Copy Token to clipboard">
                <font-awesome-icon icon="clipboard" />
              </b-button>
            </div>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.deprecateAt')"></span>
          </dt>
          <dd>
            <span v-if="token.deprecateAt">{{ formatDateLong(token.deprecateAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.roles')"></span>
          </dt>
          <dd>
            <p class="h4">
              <font-awesome-icon
                icon="at"
                :style="[1, 3, 5, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
              />
              <font-awesome-icon
                icon="sms"
                :style="[2, 3, 6, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
              />
              <font-awesome-icon
                icon="bell"
                :style="[4, 5, 6, 7].includes(token.roles) ? { color: 'var(--primary)' } : { color: 'var(--gray)' }"
              />
            </p>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.createdAt')"></span>
          </dt>
          <dd>
            <span v-if="token.createdAt">{{ formatDateLong(token.createdAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.createdBy')"></span>
          </dt>
          <dd>
            <span>{{ token.createdBy }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.updatedAt')"></span>
          </dt>
          <dd>
            <span v-if="token.updatedAt">{{ formatDateLong(token.updatedAt) }}</span>
          </dd>
          <dt>
            <span v-text="t$('messageCentralApp.token.updatedBy')"></span>
          </dt>
          <dd>
            <span>{{ token.updatedBy }}</span>
          </dd>
        </dl>
        <button type="submit" @click.prevent="$router.push({ name: 'Token' })" class="btn btn-info" data-cy="entityDetailsBackButton">
          <font-awesome-icon icon="arrow-left"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.back')"></span>
        </button>
        <button
          v-show="token.disable"
          type="submit"
          v-on:click.prevent="enableToken()"
          class="btn btn-info"
          data-cy="entityDetailsBackButton"
          ariant="info"
        >
          <font-awesome-icon icon="toggle-off"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.enable')" />
        </button>
        <button
          v-show="!token.disable"
          type="submit"
          v-on:click.prevent="disableToken()"
          class="btn btn-info"
          data-cy="entityDetailsBackButton"
          ariant="info"
        >
          <font-awesome-icon icon="toggle-on"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.disable')" />
        </button>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./token-details.component.ts"></script>

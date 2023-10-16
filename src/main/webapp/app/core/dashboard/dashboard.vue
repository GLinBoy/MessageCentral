<template>
  <div class="home row">
    <div class="col-md-3 text-center">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <b-row class="row">
        <div class="col-12">
          <h1 v-text="$t('dashboard.title')" />
        </div>
      </b-row>
      <b-row>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            :header="$t('dashboard.cards.cardsTitle.totalMessages')"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>
              <b-row class="text-center">
                <b-col lg="4">
                  <font-awesome-icon class="fa-3x" icon="comments" />
                </b-col>
                <b-col lg="8">
                  <span class="font-weight-bolder text-uppercase">Sent: {{ totalSuccessfulSent }}</span>
                  <br />
                  <span class="font-weight-lighter">Failed: {{ totalFailedSent }}</span>
                </b-col>
              </b-row>
            </b-card-text>
          </b-card>
        </b-col>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            :header="$t('dashboard.cards.cardsTitle.emails')"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>
              <b-row class="text-center">
                <b-col lg="4">
                  <router-link to="/email">
                    <font-awesome-icon class="fa-3x" icon="at" />
                  </router-link>
                </b-col>
                <b-col lg="8">
                  <span class="font-weight-bolder text-uppercase">Sent: {{ emailsSuccessfulSent }}</span>
                  <br />
                  <span class="font-weight-lighter">Failed: {{ emailsFailedSent }}</span>
                </b-col>
              </b-row>
            </b-card-text>
          </b-card>
        </b-col>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            :header="$t('dashboard.cards.cardsTitle.notifications')"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>
              <b-row class="text-center">
                <b-col lg="4">
                  <font-awesome-icon class="fa-3x" icon="bell" />
                </b-col>
                <b-col lg="8">
                  <span class="font-weight-bolder text-uppercase">Sent: {{ notificationsSuccessfulSent }}</span>
                  <br />
                  <span class="font-weight-lighter">Failed: {{ notificationsFailedSent }}</span>
                </b-col>
              </b-row>
            </b-card-text>
          </b-card>
        </b-col>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            :header="$t('dashboard.cards.cardsTitle.shortMessages')"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>
              <b-row class="text-center">
                <b-col lg="4">
                  <font-awesome-icon class="fa-3x" icon="sms" />
                </b-col>
                <b-col lg="8">
                  <span class="font-weight-bolder text-uppercase">Sent: {{ smsSuccessfulSent }}</span>
                  <br />
                  <span class="font-weight-lighter">Failed: {{ smsFailedSent }}</span>
                </b-col>
              </b-row>
            </b-card-text>
          </b-card>
        </b-col>
      </b-row>
      <div class="row">
        <div class="col-12">
          <b-card border-variant="primary">
            <div class="row">
              <div class="col-12 text-center">
                <b-button-group size="sm">
                  <b-button
                    v-for="t in messageTypeKeys"
                    :key="t"
                    variant="primary"
                    @click="loadChartData(t)"
                    :disabled="t === chartDataSelected"
                  >
                    {{ t }}
                  </b-button>
                </b-button-group>
              </div>
              <div class="col-12">
                <Bar
                  style="height: 100%"
                  :chart-id="chartId"
                  :chart-data="chartData"
                  :chart-options="chartOptions"
                  :dataset-id-key="datasetIdKey"
                  :plugins="plugins"
                  :css-classes="cssClasses"
                  :styles="styles"
                  :width="width"
                  :height="height"
                />
              </div>
            </div>
          </b-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./dashboard.component.ts"></script>

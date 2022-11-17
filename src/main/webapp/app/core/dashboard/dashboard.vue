<template>
  <div class="home row">
    <div class="col-md-3 text-center">
      <span class="hipster img-fluid rounded"></span>
    </div>
    <div class="col-md-9">
      <b-row class="row">
        <div class="col-12">
          <h1>Dashboard</h1>
        </div>
      </b-row>
      <b-row>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            header="Total Messages"
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
            header="Emails"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>
              <b-row class="text-center">
                <b-col lg="4">
                  <font-awesome-icon class="fa-3x" icon="at" />
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
            header="SMSs"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>Sent: {{ smsSuccessfulSent }}</b-card-text>
            <b-card-text>Failed: {{ smsFailedSent }}</b-card-text>
          </b-card>
        </b-col>
        <b-col sm="12" md="3">
          <b-card
            border-variant="primary"
            header="Notifications"
            header-bg-variant="primary"
            header-text-variant="white"
            align="center"
            class="mb-2"
          >
            <b-card-text>Sent: {{ notificationsSuccessfulSent }}</b-card-text>
            <b-card-text>Failed: {{ notificationsFailedSent }}</b-card-text>
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

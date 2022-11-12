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
            <b-card-text>Sent: {{ totalSuccessfulSent }}</b-card-text>
            <b-card-text>Failed: {{ totalFailedSent }}</b-card-text>
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
            <b-card-text>Sent: {{ emailsSuccessfulSent }}</b-card-text>
            <b-card-text>Failed: {{ emailsFailedSent }}</b-card-text>
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
          <Bar
            :chart-options="chartOptions"
            :chart-data="chartData"
            :chart-id="chartId"
            :dataset-id-key="datasetIdKey"
            :plugins="plugins"
            :css-classes="cssClasses"
            :styles="styles"
            :width="width"
            :height="height"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" src="./dashboard.component.ts"></script>

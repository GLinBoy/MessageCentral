/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import NotificationDataDetailComponent from '@/entities/notification-data/notification-data-details.vue';
import NotificationDataClass from '@/entities/notification-data/notification-data-details.component';
import NotificationDataService from '@/entities/notification-data/notification-data.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('NotificationData Management Detail Component', () => {
    let wrapper: Wrapper<NotificationDataClass>;
    let comp: NotificationDataClass;
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;

    beforeEach(() => {
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);

      wrapper = shallowMount<NotificationDataClass>(NotificationDataDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { notificationDataService: () => notificationDataServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundNotificationData = { id: 123 };
        notificationDataServiceStub.find.resolves(foundNotificationData);

        // WHEN
        comp.retrieveNotificationData(123);
        await comp.$nextTick();

        // THEN
        expect(comp.notificationData).toBe(foundNotificationData);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundNotificationData = { id: 123 };
        notificationDataServiceStub.find.resolves(foundNotificationData);

        // WHEN
        comp.beforeRouteEnter({ params: { notificationDataId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.notificationData).toBe(foundNotificationData);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});

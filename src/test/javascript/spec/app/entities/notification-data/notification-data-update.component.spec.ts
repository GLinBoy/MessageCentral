/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import NotificationDataUpdateComponent from '@/entities/notification-data/notification-data-update.vue';
import NotificationDataClass from '@/entities/notification-data/notification-data-update.component';
import NotificationDataService from '@/entities/notification-data/notification-data.service';

import NotificationService from '@/entities/notification/notification.service';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('NotificationData Management Update Component', () => {
    let wrapper: Wrapper<NotificationDataClass>;
    let comp: NotificationDataClass;
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;

    beforeEach(() => {
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);

      wrapper = shallowMount<NotificationDataClass>(NotificationDataUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          notificationDataService: () => notificationDataServiceStub,
          alertService: () => new AlertService(),

          notificationService: () =>
            sinon.createStubInstance<NotificationService>(NotificationService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.notificationData = entity;
        notificationDataServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationDataServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.notificationData = entity;
        notificationDataServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationDataServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundNotificationData = { id: 123 };
        notificationDataServiceStub.find.resolves(foundNotificationData);
        notificationDataServiceStub.retrieve.resolves([foundNotificationData]);

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

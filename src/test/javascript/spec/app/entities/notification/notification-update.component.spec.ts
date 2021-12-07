/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import NotificationUpdateComponent from '@/entities/notification/notification-update.vue';
import NotificationClass from '@/entities/notification/notification-update.component';
import NotificationService from '@/entities/notification/notification.service';

import NotificationDataService from '@/entities/notification-data/notification-data.service';
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
  describe('Notification Management Update Component', () => {
    let wrapper: Wrapper<NotificationClass>;
    let comp: NotificationClass;
    let notificationServiceStub: SinonStubbedInstance<NotificationService>;

    beforeEach(() => {
      notificationServiceStub = sinon.createStubInstance<NotificationService>(NotificationService);

      wrapper = shallowMount<NotificationClass>(NotificationUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          notificationService: () => notificationServiceStub,
          alertService: () => new AlertService(),

          notificationDataService: () =>
            sinon.createStubInstance<NotificationDataService>(NotificationDataService, {
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
        comp.notification = entity;
        notificationServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.notification = entity;
        notificationServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundNotification = { id: 123 };
        notificationServiceStub.find.resolves(foundNotification);
        notificationServiceStub.retrieve.resolves([foundNotification]);

        // WHEN
        comp.beforeRouteEnter({ params: { notificationId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.notification).toBe(foundNotification);
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

/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import NotificationUpdate from './notification-update.vue';
import NotificationService from './notification.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type NotificationUpdateComponentType = InstanceType<typeof NotificationUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const notificationSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<NotificationUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Notification Management Update Component', () => {
    let comp: NotificationUpdateComponentType;
    let notificationServiceStub: SinonStubbedInstance<NotificationService>;

    beforeEach(() => {
      route = {};
      notificationServiceStub = sinon.createStubInstance<NotificationService>(NotificationService);
      notificationServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          notificationService: () => notificationServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(NotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(NotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.notification = notificationSample;
        notificationServiceStub.update.resolves(notificationSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationServiceStub.update.calledWith(notificationSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        notificationServiceStub.create.resolves(entity);
        const wrapper = shallowMount(NotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.notification = entity;

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
        notificationServiceStub.find.resolves(notificationSample);
        notificationServiceStub.retrieve.resolves([notificationSample]);

        // WHEN
        route = {
          params: {
            notificationId: '' + notificationSample.id,
          },
        };
        const wrapper = shallowMount(NotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.notification).toMatchObject(notificationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        notificationServiceStub.find.resolves(notificationSample);
        const wrapper = shallowMount(NotificationUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

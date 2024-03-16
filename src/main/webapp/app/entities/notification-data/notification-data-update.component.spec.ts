/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import NotificationDataUpdate from './notification-data-update.vue';
import NotificationDataService from './notification-data.service';
import AlertService from '@/shared/alert/alert.service';

import NotificationService from '@/entities/notification/notification.service';

type NotificationDataUpdateComponentType = InstanceType<typeof NotificationDataUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const notificationDataSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<NotificationDataUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('NotificationData Management Update Component', () => {
    let comp: NotificationDataUpdateComponentType;
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;

    beforeEach(() => {
      route = {};
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);
      notificationDataServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          notificationDataService: () => notificationDataServiceStub,
          notificationService: () =>
            sinon.createStubInstance<NotificationService>(NotificationService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(NotificationDataUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.notificationData = notificationDataSample;
        notificationDataServiceStub.update.resolves(notificationDataSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(notificationDataServiceStub.update.calledWith(notificationDataSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        notificationDataServiceStub.create.resolves(entity);
        const wrapper = shallowMount(NotificationDataUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.notificationData = entity;

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
        notificationDataServiceStub.find.resolves(notificationDataSample);
        notificationDataServiceStub.retrieve.resolves([notificationDataSample]);

        // WHEN
        route = {
          params: {
            notificationDataId: '' + notificationDataSample.id,
          },
        };
        const wrapper = shallowMount(NotificationDataUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.notificationData).toMatchObject(notificationDataSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        notificationDataServiceStub.find.resolves(notificationDataSample);
        const wrapper = shallowMount(NotificationDataUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

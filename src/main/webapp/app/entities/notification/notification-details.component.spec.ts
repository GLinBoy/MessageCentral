/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import NotificationDetails from './notification-details.vue';
import NotificationService from './notification.service';
import AlertService from '@/shared/alert/alert.service';

type NotificationDetailsComponentType = InstanceType<typeof NotificationDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const notificationSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Notification Management Detail Component', () => {
    let notificationServiceStub: SinonStubbedInstance<NotificationService>;
    let mountOptions: MountingOptions<NotificationDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      notificationServiceStub = sinon.createStubInstance<NotificationService>(NotificationService);

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'router-link': true,
        },
        provide: {
          alertService,
          notificationService: () => notificationServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        notificationServiceStub.find.resolves(notificationSample);
        route = {
          params: {
            notificationId: '' + 123,
          },
        };
        const wrapper = shallowMount(NotificationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.notification).toMatchObject(notificationSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        notificationServiceStub.find.resolves(notificationSample);
        const wrapper = shallowMount(NotificationDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

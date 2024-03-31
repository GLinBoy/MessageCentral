/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import NotificationDataDetails from './notification-data-details.vue';
import NotificationDataService from './notification-data.service';
import AlertService from '@/shared/alert/alert.service';

type NotificationDataDetailsComponentType = InstanceType<typeof NotificationDataDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const notificationDataSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('NotificationData Management Detail Component', () => {
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;
    let mountOptions: MountingOptions<NotificationDataDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);

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
          notificationDataService: () => notificationDataServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        notificationDataServiceStub.find.resolves(notificationDataSample);
        route = {
          params: {
            notificationDataId: '' + 123,
          },
        };
        const wrapper = shallowMount(NotificationDataDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.notificationData).toMatchObject(notificationDataSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        notificationDataServiceStub.find.resolves(notificationDataSample);
        const wrapper = shallowMount(NotificationDataDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

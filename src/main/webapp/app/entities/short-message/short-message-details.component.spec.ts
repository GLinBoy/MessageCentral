/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import ShortMessageDetails from './short-message-details.vue';
import ShortMessageService from './short-message.service';
import AlertService from '@/shared/alert/alert.service';

type ShortMessageDetailsComponentType = InstanceType<typeof ShortMessageDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shortMessageSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('ShortMessage Management Detail Component', () => {
    let shortMessageServiceStub: SinonStubbedInstance<ShortMessageService>;
    let mountOptions: MountingOptions<ShortMessageDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      shortMessageServiceStub = sinon.createStubInstance<ShortMessageService>(ShortMessageService);

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
          shortMessageService: () => shortMessageServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        shortMessageServiceStub.find.resolves(shortMessageSample);
        route = {
          params: {
            shortMessageId: '' + 123,
          },
        };
        const wrapper = shallowMount(ShortMessageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.shortMessage).toMatchObject(shortMessageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shortMessageServiceStub.find.resolves(shortMessageSample);
        const wrapper = shallowMount(ShortMessageDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

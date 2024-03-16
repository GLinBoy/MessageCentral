/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import TokenDetails from './token-details.vue';
import TokenService from './token.service';
import AlertService from '@/shared/alert/alert.service';

type TokenDetailsComponentType = InstanceType<typeof TokenDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const tokenSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Token Management Detail Component', () => {
    let tokenServiceStub: SinonStubbedInstance<TokenService>;
    let mountOptions: MountingOptions<TokenDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      tokenServiceStub = sinon.createStubInstance<TokenService>(TokenService);

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
          tokenService: () => tokenServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        tokenServiceStub.find.resolves(tokenSample);
        route = {
          params: {
            tokenId: '' + 123,
          },
        };
        const wrapper = shallowMount(TokenDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.token).toMatchObject(tokenSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        tokenServiceStub.find.resolves(tokenSample);
        const wrapper = shallowMount(TokenDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import TokenUpdate from './token-update.vue';
import TokenService from './token.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type TokenUpdateComponentType = InstanceType<typeof TokenUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const tokenSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<TokenUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('Token Management Update Component', () => {
    let comp: TokenUpdateComponentType;
    let tokenServiceStub: SinonStubbedInstance<TokenService>;

    beforeEach(() => {
      route = {};
      tokenServiceStub = sinon.createStubInstance<TokenService>(TokenService);
      tokenServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          tokenService: () => tokenServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(TokenUpdate, { global: mountOptions });
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
        const wrapper = shallowMount(TokenUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.token = tokenSample;
        tokenServiceStub.update.resolves(tokenSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(tokenServiceStub.update.calledWith(tokenSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        tokenServiceStub.create.resolves(entity);
        const wrapper = shallowMount(TokenUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.token = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(tokenServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        tokenServiceStub.find.resolves(tokenSample);
        tokenServiceStub.retrieve.resolves([tokenSample]);

        // WHEN
        route = {
          params: {
            tokenId: '' + tokenSample.id,
          },
        };
        const wrapper = shallowMount(TokenUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.token).toMatchObject(tokenSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        tokenServiceStub.find.resolves(tokenSample);
        const wrapper = shallowMount(TokenUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

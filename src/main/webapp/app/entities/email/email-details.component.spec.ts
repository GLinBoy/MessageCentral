/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import EmailDetails from './email-details.vue';
import EmailService from './email.service';
import AlertService from '@/shared/alert/alert.service';

type EmailDetailsComponentType = InstanceType<typeof EmailDetails>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const emailSample = { id: 123 };

describe('Component Tests', () => {
  let alertService: AlertService;

  afterEach(() => {
    vitest.resetAllMocks();
  });

  describe('Email Management Detail Component', () => {
    let emailServiceStub: SinonStubbedInstance<EmailService>;
    let mountOptions: MountingOptions<EmailDetailsComponentType>['global'];

    beforeEach(() => {
      route = {};
      emailServiceStub = sinon.createStubInstance<EmailService>(EmailService);

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
          emailService: () => emailServiceStub,
        },
      };
    });

    describe('Navigate to details', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        emailServiceStub.find.resolves(emailSample);
        route = {
          params: {
            emailId: '' + 123,
          },
        };
        const wrapper = shallowMount(EmailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        // WHEN
        await comp.$nextTick();

        // THEN
        expect(comp.email).toMatchObject(emailSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        emailServiceStub.find.resolves(emailSample);
        const wrapper = shallowMount(EmailDetails, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});

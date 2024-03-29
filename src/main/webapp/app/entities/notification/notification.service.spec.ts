/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import NotificationService from './notification.service';
import { DATE_TIME_FORMAT } from '@/shared/composables/date-format';
import { Notification } from '@/shared/model/notification.model';

const error = {
  response: {
    status: null,
    data: {
      type: null,
    },
  },
};

const axiosStub = {
  get: sinon.stub(axios, 'get'),
  post: sinon.stub(axios, 'post'),
  put: sinon.stub(axios, 'put'),
  patch: sinon.stub(axios, 'patch'),
  delete: sinon.stub(axios, 'delete'),
};

describe('Service Tests', () => {
  describe('Notification Service', () => {
    let service: NotificationService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new NotificationService();
      currentDate = new Date();
      elemDefault = new Notification(123, 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'AAAAAAA', 'IN_QUEUE', currentDate, 'AAAAAAA');
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        axiosStub.get.resolves({ data: returnedFromService });

        return service.find(123).then(res => {
          expect(res).toMatchObject(elemDefault);
        });
      });

      it('should not find an element', async () => {
        axiosStub.get.rejects(error);
        return service
          .find(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should create a Notification', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            createdAt: currentDate,
          },
          returnedFromService,
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Notification', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Notification', async () => {
        const returnedFromService = Object.assign(
          {
            username: 'BBBBBB',
            token: 'BBBBBB',
            subject: 'BBBBBB',
            content: 'BBBBBB',
            image: 'BBBBBB',
            status: 'BBBBBB',
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            createdBy: 'BBBBBB',
          },
          elemDefault,
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
          },
          returnedFromService,
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Notification', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Notification', async () => {
        const patchObject = Object.assign(
          {
            image: 'BBBBBB',
            status: 'BBBBBB',
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          new Notification(),
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            createdAt: currentDate,
          },
          returnedFromService,
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Notification', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Notification', async () => {
        const returnedFromService = Object.assign(
          {
            username: 'BBBBBB',
            token: 'BBBBBB',
            subject: 'BBBBBB',
            content: 'BBBBBB',
            image: 'BBBBBB',
            status: 'BBBBBB',
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            createdBy: 'BBBBBB',
          },
          elemDefault,
        );
        const expected = Object.assign(
          {
            createdAt: currentDate,
          },
          returnedFromService,
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Notification', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Notification', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Notification', async () => {
        axiosStub.delete.rejects(error);

        return service
          .delete(123)
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });
    });
  });
});

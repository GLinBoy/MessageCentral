/* tslint:disable max-line-length */
import axios from 'axios';
import sinon from 'sinon';
import dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from '@/shared/date/filters';
import TokenService from '@/entities/token/token.service';
import { Token } from '@/shared/model/token.model';

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
  describe('Token Service', () => {
    let service: TokenService;
    let elemDefault;
    let currentDate: Date;

    beforeEach(() => {
      service = new TokenService();
      currentDate = new Date();
      elemDefault = new Token(123, 'AAAAAAA', 'AAAAAAA', false, currentDate, currentDate, 0);
    });

    describe('Service methods', () => {
      it('should find an element', async () => {
        const returnedFromService = Object.assign(
          {
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deprecateAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault
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

      it('should create a Token', async () => {
        const returnedFromService = Object.assign(
          {
            id: 123,
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deprecateAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            createdAt: currentDate,
            deprecateAt: currentDate,
          },
          returnedFromService
        );

        axiosStub.post.resolves({ data: returnedFromService });
        return service.create({}).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not create a Token', async () => {
        axiosStub.post.rejects(error);

        return service
          .create({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should update a Token', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            token: 'BBBBBB',
            disable: true,
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deprecateAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            roles: 1,
          },
          elemDefault
        );

        const expected = Object.assign(
          {
            createdAt: currentDate,
            deprecateAt: currentDate,
          },
          returnedFromService
        );
        axiosStub.put.resolves({ data: returnedFromService });

        return service.update(expected).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not update a Token', async () => {
        axiosStub.put.rejects(error);

        return service
          .update({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should partial update a Token', async () => {
        const patchObject = Object.assign(
          {
            name: 'BBBBBB',
          },
          new Token()
        );
        const returnedFromService = Object.assign(patchObject, elemDefault);

        const expected = Object.assign(
          {
            createdAt: currentDate,
            deprecateAt: currentDate,
          },
          returnedFromService
        );
        axiosStub.patch.resolves({ data: returnedFromService });

        return service.partialUpdate(patchObject).then(res => {
          expect(res).toMatchObject(expected);
        });
      });

      it('should not partial update a Token', async () => {
        axiosStub.patch.rejects(error);

        return service
          .partialUpdate({})
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should return a list of Token', async () => {
        const returnedFromService = Object.assign(
          {
            name: 'BBBBBB',
            token: 'BBBBBB',
            disable: true,
            createdAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            deprecateAt: dayjs(currentDate).format(DATE_TIME_FORMAT),
            roles: 1,
          },
          elemDefault
        );
        const expected = Object.assign(
          {
            createdAt: currentDate,
            deprecateAt: currentDate,
          },
          returnedFromService
        );
        axiosStub.get.resolves([returnedFromService]);
        return service.retrieve({ sort: {}, page: 0, size: 10 }).then(res => {
          expect(res).toContainEqual(expected);
        });
      });

      it('should not return a list of Token', async () => {
        axiosStub.get.rejects(error);

        return service
          .retrieve()
          .then()
          .catch(err => {
            expect(err).toMatchObject(error);
          });
      });

      it('should delete a Token', async () => {
        axiosStub.delete.resolves({ ok: true });
        return service.delete(123).then(res => {
          expect(res.ok).toBeTruthy();
        });
      });

      it('should not delete a Token', async () => {
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

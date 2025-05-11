import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';

import { type IShortMessage, type IShortMessages } from '@/shared/model/short-message.model';

const baseApiUrl = 'api/short-messages';

export default class ShortMessageService {
  find(id: number): Promise<IShortMessage> {
    return new Promise<IShortMessage>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}?${buildPaginationQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  create(entity: IShortMessage): Promise<IShortMessage> {
    return new Promise<IShortMessage>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  update(entity: IShortMessage): Promise<IShortMessage> {
    return new Promise<IShortMessage>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  partialUpdate(entity: IShortMessage): Promise<IShortMessage> {
    return new Promise<IShortMessage>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public createMultiple(entity: IShortMessages[]): Promise<any> {
    return new Promise((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/multiple`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}

import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';
import buildSearchQueryOpts from '@/shared/search/searchs';

import { IEmail, IEmails } from '@/shared/model/email.model';

const baseApiUrl = 'api/emails';

export default class EmailService {
  public find(id: number): Promise<IEmail> {
    return new Promise<IEmail>((resolve, reject) => {
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

  public retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl + `?${buildPaginationQueryOpts(paginationQuery)}` + `${buildSearchQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
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

  public create(entity: IEmail): Promise<IEmail> {
    return new Promise<IEmail>((resolve, reject) => {
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

  public update(entity: IEmail): Promise<IEmail> {
    return new Promise<IEmail>((resolve, reject) => {
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

  public partialUpdate(entity: IEmail): Promise<IEmail> {
    return new Promise<IEmail>((resolve, reject) => {
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

  /**
   * Create multiple Emails at once
   */
  public createMultiple(entity: IEmails[]): Promise {
    return new Promise((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/multiple`, entity)
        .then(res => {
          resolve();
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}

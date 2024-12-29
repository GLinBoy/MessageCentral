import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('NotificationData e2e test', () => {
  const notificationDataPageUrl = '/notification-data';
  const notificationDataPageUrlPattern = new RegExp('/notification-data(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const notificationDataSample = { dataKey: 'lucky', dataValue: 'willfully' };

  let notificationData;
  let notification;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/notifications',
      body: {
        username: 'boo vague between',
        token: 'famously unzip',
        subject: 'mid meh',
        content: 'meager',
        image: 'fooey bravely',
        status: 'IN_QUEUE',
        createdAt: '2024-12-28T00:49:36.820Z',
        createdBy: 'likewise',
      },
    }).then(({ body }) => {
      notification = body;
    });
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/notification-data+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notification-data').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notification-data/*').as('deleteEntityRequest');
  });

  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/notifications', {
      statusCode: 200,
      body: [notification],
    });
  });

  afterEach(() => {
    if (notificationData) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notification-data/${notificationData.id}`,
      }).then(() => {
        notificationData = undefined;
      });
    }
  });

  afterEach(() => {
    if (notification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notifications/${notification.id}`,
      }).then(() => {
        notification = undefined;
      });
    }
  });

  it('NotificationData menu should load NotificationData page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('notification-data');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('NotificationData').should('exist');
    cy.url().should('match', notificationDataPageUrlPattern);
  });

  describe('NotificationData page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notificationDataPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create NotificationData page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/notification-data/new$'));
        cy.getEntityCreateUpdateHeading('NotificationData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationDataPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notification-data',
          body: {
            ...notificationDataSample,
            notification,
          },
        }).then(({ body }) => {
          notificationData = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notification-data+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [notificationData],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(notificationDataPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details NotificationData page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notificationData');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationDataPageUrlPattern);
      });

      it('edit button click should load edit NotificationData page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NotificationData');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationDataPageUrlPattern);
      });

      it('edit button click should load edit NotificationData page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('NotificationData');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationDataPageUrlPattern);
      });

      it('last delete button click should delete instance of NotificationData', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('notificationData').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationDataPageUrlPattern);

        notificationData = undefined;
      });
    });
  });

  describe('new NotificationData page', () => {
    beforeEach(() => {
      cy.visit(`${notificationDataPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('NotificationData');
    });

    it('should create an instance of NotificationData', () => {
      cy.get(`[data-cy="dataKey"]`).type('devoted');
      cy.get(`[data-cy="dataKey"]`).should('have.value', 'devoted');

      cy.get(`[data-cy="dataValue"]`).type('narrate');
      cy.get(`[data-cy="dataValue"]`).should('have.value', 'narrate');

      cy.get(`[data-cy="notification"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        notificationData = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', notificationDataPageUrlPattern);
    });
  });
});

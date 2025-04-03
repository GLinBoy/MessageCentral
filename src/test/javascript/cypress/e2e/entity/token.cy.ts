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

describe('Token e2e test', () => {
  const tokenPageUrl = '/token';
  const tokenPageUrlPattern = new RegExp('/token(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const tokenSample = {
    name: 'pfft amidst',
    token: 'sheepishly immediately sarcastic',
    disable: false,
    deprecateAt: '2024-12-28T02:12:44.492Z',
    roles: 12357,
    createdAt: '2024-12-27T22:14:16.871Z',
    createdBy: 'where infinite atop',
    updatedAt: '2024-12-28T00:06:57.220Z',
    updatedBy: 'surprise',
  };

  let token;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tokens+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tokens').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tokens/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (token) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tokens/${token.id}`,
      }).then(() => {
        token = undefined;
      });
    }
  });

  it('Tokens menu should load Tokens page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('token');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Token').should('exist');
    cy.url().should('match', tokenPageUrlPattern);
  });

  describe('Token page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tokenPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Token page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/token/new$'));
        cy.getEntityCreateUpdateHeading('Token');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tokenPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tokens',
          body: tokenSample,
        }).then(({ body }) => {
          token = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tokens+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/tokens?page=0&size=20>; rel="last",<http://localhost/api/tokens?page=0&size=20>; rel="first"',
              },
              body: [token],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tokenPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Token page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('token');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tokenPageUrlPattern);
      });

      it('edit button click should load edit Token page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Token');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tokenPageUrlPattern);
      });

      it('edit button click should load edit Token page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Token');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tokenPageUrlPattern);
      });

      it('last delete button click should delete instance of Token', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('token').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tokenPageUrlPattern);

        token = undefined;
      });
    });
  });

  describe('new Token page', () => {
    beforeEach(() => {
      cy.visit(`${tokenPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Token');
    });

    it('should create an instance of Token', () => {
      cy.get(`[data-cy="name"]`).type('yuck whitewash endow');
      cy.get(`[data-cy="name"]`).should('have.value', 'yuck whitewash endow');

      cy.get(`[data-cy="token"]`).type('ick');
      cy.get(`[data-cy="token"]`).should('have.value', 'ick');

      cy.get(`[data-cy="disable"]`).should('not.be.checked');
      cy.get(`[data-cy="disable"]`).click();
      cy.get(`[data-cy="disable"]`).should('be.checked');

      cy.get(`[data-cy="deprecateAt"]`).type('2024-12-27T17:03');
      cy.get(`[data-cy="deprecateAt"]`).blur();
      cy.get(`[data-cy="deprecateAt"]`).should('have.value', '2024-12-27T17:03');

      cy.get(`[data-cy="roles"]`).type('29683');
      cy.get(`[data-cy="roles"]`).should('have.value', '29683');

      cy.get(`[data-cy="createdAt"]`).type('2024-12-27T18:47');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2024-12-27T18:47');

      cy.get(`[data-cy="createdBy"]`).type('wilt pearl');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'wilt pearl');

      cy.get(`[data-cy="updatedAt"]`).type('2024-12-28T08:58');
      cy.get(`[data-cy="updatedAt"]`).blur();
      cy.get(`[data-cy="updatedAt"]`).should('have.value', '2024-12-28T08:58');

      cy.get(`[data-cy="updatedBy"]`).type('anenst');
      cy.get(`[data-cy="updatedBy"]`).should('have.value', 'anenst');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        token = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', tokenPageUrlPattern);
    });
  });
});

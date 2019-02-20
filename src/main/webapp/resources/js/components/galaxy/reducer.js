export const types = {
  EMAIL_UPDATED: "GALAXY/SET_GALAXY_EMAIL",
  MAKE_PAIRED_COLLECTION_UPDATED: "GALAXY/MAKE_PAIRED_COLLECTION_UPDATED",
  GET_GALAXY_SAMPLES: "GALAXY/GET_GALAXY_SAMPLES",
  SET_GALAXY_SAMPLES: "GALAXY/SET_GALAXY_SAMPLES",
  CHECK_OAUTH: "GALAXY/CHECK_OAUTH",
  SET_OAUTH_AUTHENTICATION: "GALAXY/CHECK_OAUTH_VALIDATION",
  AUTHENTICATE_OATH: "GALAXY/AUTHENTICATE_OAUTH",
  OAUTH_COMPLETE: "GALAXY/OAUTH_COMPLETE",
  OAUTH_SUCCESS: "GALAXY/OAUTH_SUCCESS",
  OAUTH_ERROR: "GALAXY/OAUTH_ERROR",
  SUBMITTABLE: "GALAXY/SUBMITTABLE",
  SUBMIT: "GALAXY/SUBMIT"
};

const initialState = {
  email: window.PAGE.user,
  makepairedcollection: true,
  samples: undefined,
  oauthAuthorized: undefined,
  oauthError: false,
  submittable: false
};

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.EMAIL_UPDATED:
      return { ...state, email: action.payload.email };
    case types.MAKE_PAIRED_COLLECTION_UPDATED:
      return {
        ...state,
        makepairedcollection: action.payload.makepairedcollection
      };
    case types.SET_GALAXY_SAMPLES:
      return { ...state, samples: action.payload.samples };
    case types.SET_OAUTH_AUTHENTICATION:
      return { ...state, oauthAuthorized: action.payload.isAuthorized };
    case types.OAUTH_SUCCESS:
      return {
        ...state,
        redirect: action.payload.redirect,
        code: action.payload.code,
        oauthAuthorized: true
      };
    case types.OAUTH_ERROR:
      return { ...state, oauthError: true };
    case types.SUBMITTABLE:
      return { ...state, submittable: true };
    default:
      return { ...state };
  }
};

export const actions = {
  setEmail: email => ({ type: types.EMAIL_UPDATED, payload: { email } }),
  setMakePairedCollection: makepairedcollection => ({
    type: types.MAKE_PAIRED_COLLECTION_UPDATED,
    payload: { makepairedcollection }
  }),
  getGalaxySamples: () => ({ type: types.GET_GALAXY_SAMPLES }),
  setGalaxySamples: samples => ({
    type: types.SET_GALAXY_SAMPLES,
    payload: { samples }
  }),
  checkOauthStatus: () => ({ type: types.CHECK_OAUTH }),
  setOathValidationStatus: isAuthorized => ({
    type: types.SET_OAUTH_AUTHENTICATION,
    payload: {
      isAuthorized
    }
  }),
  authenticateOauthClient: () => ({
    type: types.AUTHENTICATE_OATH
  }),
  oauthSuccess: (code, redirect) => ({
    type: types.OAUTH_SUCCESS,
    payload: {
      code,
      redirect
    }
  }),
  oauthError: () => ({
    type: types.OAUTH_ERROR
  }),
  oauthComplete: () => ({
    type: types.OAUTH_COMPLETE
  }),
  enableSubmit: () => ({
    type: types.SUBMITTABLE
  }),
  submit: () => ({
    type: types.SUBMIT
  })
};

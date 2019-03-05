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
      return { ...state, samples: action.payload.samples, submittable: true };
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
  submit: (email, makepairedcollection, samples) => ({
    type: types.SUBMIT,
    payload: {
      email,
      makepairedcollection,
      samples
    }
  })
};

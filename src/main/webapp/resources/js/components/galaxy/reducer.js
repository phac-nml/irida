export const types = {
  EMAIL_UPDATED: "GALAXY/SET_GALAXY_EMAIL",
  MAKE_PAIRED_COLLECTION_UPDATED: "GALAXY/MAKE_PAIRED_COLLECTION_UPDATED",
  GET_GALAXY_SAMPLES: "GALAXY/GET_GALAXY_SAMPLES",
  SET_GALAXY_SAMPLES: "GALAXY/SET_GALAXY_SAMPLES",
  OAUTH_WINDOW_CLOSED: "GALAXY_OAUTH_WINDOW_CLOSED",
  SUBMITTABLE: "GALAXY/SUBMITTABLE",
  SUBMIT: "GALAXY/SUBMIT",
  SUBMIT_ERROR: "GALAXY/SUBMIT_ERROR"
};

const initialState = {
  email: window.PAGE.user,
  makepairedcollection: true,
  samples: undefined,
  submitted: false,
  submittable: false,
  errored: false
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
    case types.OAUTH_WINDOW_CLOSED:
      return { ...state, submittable: true, submitted: false };
    case types.SUBMIT:
      return { ...state, submitted: true };
    case types.SUBMIT_ERROR:
      return { ...state, submittable: true, submitted: false, errored: true };
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
  }),
  submitError: () => ({
    type: types.SUBMIT_ERROR
  }),
  oauthWindowClosed: () => ({
    type: types.OAUTH_WINDOW_CLOSED
  })
};

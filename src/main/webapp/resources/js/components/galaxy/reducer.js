import { validateEmail } from "../../utilities/validation-utilities";

export const types = {
  EMAIL_UPDATED: "GALAXY/SET_GALAXY_EMAIL",
  MAKE_PAIRED_COLLECTION_UPDATED: "GALAXY/MAKE_PAIRED_COLLECTION_UPDATED",
  OAUTH_WINDOW_CLOSED: "GALAXY_OAUTH_WINDOW_CLOSED",
  SUBMITTABLE: "GALAXY/SUBMITTABLE",
  SUBMIT: "GALAXY/SUBMIT",
  SUBMIT_ERROR: "GALAXY/SUBMIT_ERROR"
};

export const initialState = {
  email: window.PAGE.user,
  validEmail: true, // Default to true since from IRIDA
  makepairedcollection: true,
  fetchingSamples: false,
  submitted: false,
  submittable: true,
  errored: false
};

export const reducer = (state, action) => {
  switch (action.type) {
    case types.EMAIL_UPDATED:
      return {
        ...state,
        email: action.payload.email,
        validEmail: validateEmail(action.payload.email)
      };
    case types.MAKE_PAIRED_COLLECTION_UPDATED:
      return {
        ...state,
        makepairedcollection: action.payload.makepairedcollection
      };
    case types.OAUTH_WINDOW_CLOSED:
      return { ...state, submittable: true, submitted: false };
    case types.SUBMIT:
      return { ...state, fetchingSamples: true, submitted: true };
    case types.SUBMIT_ERROR:
      return {
        ...state,
        fetchingSamples: false,
        submittable: true,
        submitted: false,
        errored: true
      };
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
  submit: () => ({
    type: types.SUBMIT
  }),
  submitError: () => ({
    type: types.SUBMIT_ERROR
  }),
  oauthWindowClosed: () => ({
    type: types.OAUTH_WINDOW_CLOSED
  })
};

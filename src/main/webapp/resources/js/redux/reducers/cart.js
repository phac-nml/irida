import { CART } from "../../utilities/events-utilities";

export const types = {
  INITIALIZED: "CART/INITIALIZED",
  ADD: "CART/ADD",
  UPDATED: "CART/UPDATED",
  CART_EMPTY: "CART/EMPTY",
  CART_EMPTY_SUCCESS: "CART/EMPTY_SUCCESS",
  REMOVE_SAMPLE: "CART/REMOVE_SAMPLE",
  REMOVE_PROJECT: "CART/REMOVE_PROJECT",
  LOAD_CART: "CART/LOAD_CART",
  CART_LOADED: "CART/CART_LOADED",
  APPLY_FILTER: "CART/FILTER"
};

const initialState = {
  count: 0,
  initialized: false,
  loaded: false,
  filter: "",
  samples: [],
  filteredSamples: []
};

function filterSamples(samples = [], filter = "") {
  return filter.length > 0
    ? samples.filter(s => s.label.toLowerCase().includes(filter))
    : samples;
}

function removeSample(samples, filter, projectId, sampleId) {
  const updatedSamples = [...samples];
  const index = updatedSamples.findIndex(
    sample => sample.project.id === projectId && sample.id === sampleId
  );
  updatedSamples.splice(index, 1);
  return {
    samples: updatedSamples,
    filteredSamples: filterSamples(updatedSamples, filter)
  };
}

function removeProject(samples, filter, projectId) {
  const filtered = samples.filter(s => s.project.id !== projectId);
  return {
    samples: filtered,
    filteredSamples: filterSamples(filtered, filter)
  };
}

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.INITIALIZED:
      return { ...state, ...{ count: action.count, initialized: true } };
    case types.UPDATED:
      /*
      Since the cart is not currently a react component, setting the state does
      nothing.  We are going to use a CustomEvent so that we can communicate with
      the current AngularJS controller for the navigation.
       */
      document.dispatchEvent(
        new CustomEvent(CART.UPDATED, {
          detail: action.payload
        })
      );
      return { ...state, ...{ count: action.payload.count } };
    case types.CART_LOADED:
      return {
        ...state,
        loaded: true,
        samples: action.payload.samples,
        filteredSamples: action.payload.samples
      };
    case types.REMOVE_SAMPLE:
      return {
        ...state,
        ...removeSample(
          state.samples,
          state.filter,
          action.payload.projectId,
          action.payload.sampleId
        )
      };
    case types.REMOVE_PROJECT:
      return {
        ...state,
        ...removeProject(state.samples, state.filter, action.payload.id)
      };
    case types.APPLY_FILTER:
      return {
        ...state,
        filter: action.payload.filter.toLowerCase(),
        filteredSamples: filterSamples(
          state.samples,
          action.payload.filter.toLowerCase()
        )
      };
    case types.CART_EMPTY_SUCCESS:
      return { ...state, count: 0 };
    default:
      return { ...state };
  }
};

export const actions = {
  initialized: count => ({ type: types.INITIALIZED, count }),
  add: samples => ({ type: types.ADD, samples }),
  updated: response => ({ type: types.UPDATED, payload: response }),
  emptyCart: () => ({ type: types.CART_EMPTY }),
  removeSample: (projectId, sampleId) => ({
    type: types.REMOVE_SAMPLE,
    payload: {
      projectId,
      sampleId
    }
  }),
  removeProject: id => ({
    type: types.REMOVE_PROJECT,
    payload: {
      id
    }
  }),
  loadCart: () => ({
    type: types.LOAD_CART
  }),
  cartLoaded: samples => ({
    type: types.CART_LOADED,
    payload: { samples }
  }),
  applyFilter: filter => ({
    type: types.APPLY_FILTER,
    payload: {
      filter
    }
  })
};

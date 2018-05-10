import { List, fromJS } from "immutable";

export const types = {
  LOAD: "METADATA/TEMPLATES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/TEMPLATES/LOAD_TEMPLATES_ERROR",
  LOAD_SUCCESS: "METADATA/TEMPLATES/LOAD_TEMPLATES_SUCCESS",
  USE_TEMPLATE: "METADATA/TEMPLATES/USE_TEMPLATE"
};

const initialState = fromJS({
  fetching: false,
  error: false,
  templates: List(),
  current: -1
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("fetching", true).set("error", false);
    case types.LOAD_SUCCESS:
      return state
        .set("fetching", false)
        .set("templates", fromJS(action.templates));
    case types.LOAD_ERROR:
      return state.set("fetching", false).set("error", true);
    case types.USE_TEMPLATE:
      return state.set("current", action.index);
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: templates => ({ type: types.LOAD_SUCCESS, templates }),
  error: error => ({ type: types.LOAD_ERROR, error }),
  use: index => ({ type: types.USE_TEMPLATE, index })
};

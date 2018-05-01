export const types = {
  LOAD: "METADATA/TEMPLATES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/TEMPLATES/LOAD_TEMPLATES_ERROR",
  LOAD_SUCCESS: "METADATA/TEMPLATES/LOAD_TEMPLATES_SUCCESS"
};

const initialState = {
  fetching: false,
  error: null,
  templates: []
};

export default (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return { ...state, fetching: true, error: null };
    case types.LOAD_SUCCESS:
      return { ...state, fetching: false, templates: action.templates };
    case types.LOAD_ERROR:
      return { ...state, fetching: false, error: action.error };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: templates => ({ type: types.LOAD_SUCCESS, templates }),
  error: error => ({ type: types.LOAD_ERROR, error })
};

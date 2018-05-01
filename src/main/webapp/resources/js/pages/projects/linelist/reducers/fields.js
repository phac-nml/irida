export const types = {
  LOAD: "METADATA/FIELDS/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/FIELDS/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/FIELDS/LOAD_SUCCESS"
};

export const initialState = {
  initializing: true, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  fields: null, // List of metadata fields ==> used for table headers
  entries: null // list of metadata entries ==> table content.
};

export default (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return { ...state, initializing: true, error: null };
    case types.LOAD_SUCCESS:
      return {
        ...state,
        initializing: false,
        error: false,
        fields: action.fields,
        entries: action.entries
      };
    case types.LOAD_ERROR:
      return {
        ...state,
        initializing: false,
        error: true,
        fields: null,
        entries: null
      };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: fields => ({ type: types.LOAD_SUCCESS, fields }),
  error: error => ({ type: types.LOAD_ERROR, error })
};

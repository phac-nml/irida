export const types = {
  LOAD: "METADATA/ENTRIES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/ENTRIES/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/ENTRIES/LOAD_SUCCESS"
};

export const initialState = {
  fetching: false, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  entries: null // List of metadata entries
};

/*
REDUCERS
 */
export default (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return { ...state, fetching: true, error: null };
    case types.LOAD_SUCCESS:
      return {
        ...state,
        fetching: false,
        error: false,
        entries: action.entries
      };
    case types.LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        error: true,
        entries: null
      };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: entries => ({ type: types.LOAD_SUCCESS, entries }),
  error: error => ({ type: types.LOAD_ERROR, error })
};

/*
SAGAS
 */

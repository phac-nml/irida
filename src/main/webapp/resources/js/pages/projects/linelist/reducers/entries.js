import { List, fromJS } from "immutable";

export const types = {
  LOAD: "METADATA/ENTRIES/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/ENTRIES/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/ENTRIES/LOAD_SUCCESS"
};

export const initialState = fromJS({
  fetching: false, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  entries: List() // List of metadata entriesfi
});

/*
REDUCERS
 */
export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("fetching", true).set("error", false);
    case types.LOAD_SUCCESS:
      return state
        .set("fetching", false)
        .set("error", false)
        .set("entries", fromJS(action.entries));
    case types.LOAD_ERROR:
      return state.set("fetching", false).set("error", true);
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: entries => ({ type: types.LOAD_SUCCESS, entries }),
  error: error => ({ type: types.LOAD_ERROR, error })
};

import { Map, List } from "immutable";

/**
 * Fields need to be formatted properly to go into the column headers.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = cols =>
  cols.map(f => ({
    field: f.label,
    headerName: f.label.toUpperCase()
  }));

export const types = {
  LOAD: "METADATA/FIELDS/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/FIELDS/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/FIELDS/LOAD_SUCCESS"
};

export const initialState = Map({
  initializing: true, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  fields: List() // List of metadata fields ==> used for table headers
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("initializing", true).set("error", false);
    case types.LOAD_SUCCESS:
      const fields = List.of(formatColumns(action.fields));
      return state
        .set("initializing", false)
        .set("error", false)
        .set("fields", fields);
    case types.LOAD_ERROR:
      return state
        .set("initializing", false)
        .set("error", false)
        .set("fields", List());
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  success: fields => ({ type: types.LOAD_SUCCESS, fields }),
  error: error => ({ type: types.LOAD_ERROR, error })
};

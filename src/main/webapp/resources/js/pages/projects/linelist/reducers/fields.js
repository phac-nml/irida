import { List, fromJS } from "immutable";

/*
Special handler for formatting the sample Name Column;
 */
const sampleNameColumn = {
  sort: "asc",
  pinned: "left",
  cellRenderer: "SampleNameRenderer",
  checkboxSelection: true,
  headerCheckboxSelection: true,
  headerCheckboxSelectionFilteredOnly: true
};

/**
 * Fields need to be formatted properly to go into the column headers.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = cols =>
  cols.map((f, i) => ({
    field: f.label,
    headerName: f.label,
    lockPinned: true,
    ...(i === 0 ? sampleNameColumn : {})
  }));

export const types = {
  LOAD: "METADATA/FIELDS/LOAD_REQUEST",
  LOAD_ERROR: "METADATA/FIELDS/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/FIELDS/LOAD_SUCCESS"
};

export const initialState = fromJS({
  initializing: true, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  fields: List() // List of metadata fields ==> used for table headers
});

export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return state.set("initializing", true).set("error", false);
    case types.LOAD_SUCCESS:
      return state
        .set("initializing", false)
        .set("error", false)
        .delete("fields")
        .set("fields", fromJS(formatColumns(action.fields)));
    case types.LOAD_ERROR:
      return state
        .set("initializing", false)
        .set("error", true)
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

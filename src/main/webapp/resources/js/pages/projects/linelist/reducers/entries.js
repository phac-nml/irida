export const types = {
  LOAD: "METADATA/ENTRIES/LOAD_REQUEST",
  LOADING: "METADATA/ENTRIES/LOADING",
  LOAD_ERROR: "METADATA/ENTRIES/LOAD_ERROR",
  LOAD_SUCCESS: "METADATA/ENTRIES/LOAD_SUCCESS",
  SELECTION: "METADATA/ENTRIES/SELECTION",
  EDITED: "METADATA/ENTRIES/EDITED",
  FILTER: "METADATA/ENTRIES/FILTER",
};

const { totalSamples } = window.PAGE;

export const initialState = {
  fetching: false, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  entries: null, // List of metadata entries
  selected: [],
  globalFilter: "",
  loading: { count: 0, total: totalSamples },
};

/*
REDUCERS
 */
export const reducer = (state = initialState, action = {}) => {
  switch (action.type) {
    case types.LOAD:
      return { ...state, fetching: true, error: false };
    case types.LOADING:
      return {
        ...state,
        loading: {
          count: state.loading.count + action.payload.count,
          total: action.payload.total,
        },
      };
    case types.LOAD_SUCCESS:
      return {
        ...state,
        fetching: false,
        error: false,
        entries: action.entries,
        loading: false,
      };
    case types.LOAD_ERROR:
      return { ...state, fetching: false, error: true };
    case types.SELECTION:
      return { ...state, selected: action.selected };
    case types.FILTER:
      return { ...state, globalFilter: action.filter };
    default:
      return state;
  }
};

export const actions = {
  load: () => ({ type: types.LOAD }),
  loading: (count, total) => ({
    type: types.LOADING,
    payload: { count, total },
  }),
  success: (entries) => ({ type: types.LOAD_SUCCESS, entries }),
  error: (error) => ({ type: types.LOAD_ERROR, error }),
  selection: (selected) => ({
    type: types.SELECTION,
    selected,
  }),
  edited: (entry, field, label) => ({
    type: types.EDITED,
    entry,
    field,
    label,
  }),
  setGlobalFilter: (value) => ({ type: types.FILTER, filter: value }),
};

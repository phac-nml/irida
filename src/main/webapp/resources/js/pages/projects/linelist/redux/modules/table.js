import { call, put } from "redux-saga/effects";

const LOAD = "linelist/table/LOAD_REQUEST";
const LOAD_ERROR = "linelist/table/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/table/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = {
  fetching: false, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  fields: null, // List of metadata fields ==> used for table headers
  entries: null // list of metadata entries ==> table content.
};

/*
REDUCERS - Handle updating the state based on the action that is passed.
 */
export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD:
      return { ...state, fetching: true, error: null };
    case LOAD_SUCCESS:
      return {
        ...state,
        fetching: false,
        error: false,
        fields: action.fields,
        entries: action.entries
      };
    case LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        error: true,
        fields: null,
        entries: null
      };
    default:
      return state;
  }
}

/*
ACTIONS
 */
function load() {
  return {
    type: LOAD
  };
}

function loadSuccess({ fields, entries }) {
  return {
    type: LOAD_SUCCESS,
    fields,
    entries
  };
}

function loadError(error) {
  return {
    type: LOAD_ERROR,
    error
  };
}

/*
SAGAS
 */

/**
 * Fetch all the metadata fields and entries required to initialize the table.
 * @param {function} fetchFields api to fetch metadata fields for the project
 * @param {function} fetchEntries api to fetch metadata entries for the project.
 * @param {Number} projectId project identifier
 * @returns {IterableIterator<*>}
 */
export function* tableInitializer(fetchFields, fetchEntries, projectId) {
  try {
    yield put(load());
    const { data: fields } = yield call(fetchFields, projectId);
    const { data: entries } = yield call(fetchEntries, projectId);
    yield put(loadSuccess({ fields, entries }));
  } catch (error) {
    yield put(loadError(error));
  }
}

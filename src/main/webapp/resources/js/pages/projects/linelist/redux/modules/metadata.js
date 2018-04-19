import { call, put } from "redux-saga/effects";
import { fetchMetadataFields, fetchMetadataEntries } from "../../apis";

const LOAD = "linelist/metadata/LOAD_REQUEST";
const LOAD_ERROR = "linelist/metadata/field/LOAD_ERROR";
const LOAD_FIELD_SUCCESS = "linelist/metadata/field/LOAD_FIELD_SUCCESS";
const LOAD_ENTRY_SUCCESS = "linelist/metadata/entry/LOAD_ENTRY_SUCCESS";

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
This is the **ONLY** place the updates can be made to the state of metadata
fields and entries.
 */
export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD:
      return { ...state, fetching: true, error: null };
    case LOAD_FIELD_SUCCESS:
      return {
        ...state,
        fetching: false,
        error: false,
        fields: action.fields,
        entries: null
      };
    case LOAD_ENTRY_SUCCESS:
      return {
        ...state,
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

function loadFieldSuccess(fields) {
  return {
    type: LOAD_FIELD_SUCCESS,
    fields
  };
}

function loadEntrySuccess(entries) {
  return {
    type: LOAD_ENTRY_SUCCESS,
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
 * @param {Number} projectId project identifier
 * @returns {IterableIterator<*>}
 */
export function* metadataLoadingSaga(projectId) {
  try {
    yield put(load());
    const { data: fields } = yield call(fetchMetadataFields, projectId);
    yield put(loadFieldSuccess(fields));
    const { data: entries } = yield call(fetchMetadataEntries, projectId);
    yield put(loadEntrySuccess(entries));
  } catch (error) {
    yield put(loadError(error));
  }
}

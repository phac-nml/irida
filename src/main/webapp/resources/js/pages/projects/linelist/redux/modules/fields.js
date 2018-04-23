import { call, put, take } from "redux-saga/effects";
import { fetchMetadataFields } from "../../apis";
import { INIT_APP } from "./app";

const LOAD = "linelist/fields/LOAD_REQUEST";
const LOAD_ERROR = "linelist/fields/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/fields/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = {
  fetching: false, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  fields: null // List of metadata fields ==> used for table headers
};

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

/*
REDUCERS - Handle updating the state based on the action that is passed.
This is the **ONLY** place the updates can be made to the state of metadata
fields and entries.
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
        fields: action.fields
      };
    case LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        error: true,
        fields: null
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

function loadSuccess({ fields }) {
  return {
    type: LOAD_SUCCESS,
    fields
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
 * Fetch all the metadata fields required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* metadataLoadingSaga() {
  try {
    const { id } = yield take(INIT_APP);
    yield put(load());
    const { data } = yield call(fetchMetadataFields, id);
    const fields = formatColumns(data);
    yield put(loadSuccess({ fields }));
  } catch (error) {
    yield put(loadError(error));
  }
}

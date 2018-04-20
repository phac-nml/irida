import { call, put, take } from "redux-saga/effects";
import { fetchMetadataEntries } from "../../apis";
import { INIT_APP } from "./app";

const LOAD = "linelist/entries/LOAD_REQUEST";
const LOAD_ERROR = "linelist/entries/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/entries/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = {
  fetching: false, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  entries: null // List of metadata entries
};

/*
REDUCERS
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
        entries: action.entries
      };
    case LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        error: true,
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

function loadSuccess({ entries }) {
  return {
    type: LOAD_SUCCESS,
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
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  try {
    const { id } = yield take(INIT_APP);
    yield put(load());
    const { data: entries } = yield call(fetchMetadataEntries, id);
    yield put(loadSuccess({ entries }));
  } catch (error) {
    yield put(loadError(error));
  }
}

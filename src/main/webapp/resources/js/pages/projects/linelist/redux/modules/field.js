import axios from "axios";
import { takeLatest, call, put } from "redux-saga/effects";
import { INITIALIZE_APP } from "../../app/actions";

const LOAD = "linelist/field/LOAD_REQUEST";
const LOAD_ERROR = "linelist/field/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/field/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = {
  fetching: false,
  fields: null,
  error: null
};

/*
REDUCERS
 */
export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD:
      return { ...state, fetching: true, error: null };
    case LOAD_SUCCESS:
      return { ...state, fetching: false, error: false, fields: action.fields };
    case LOAD_ERROR:
      return { ...state, fetching: false, error: true, fields: null };
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

/*
SAGAS
 */

/**
 * Watcher Saga: watches for actions dispatched to the store, and start the
 * appropriate saga.
 * @param {{api, id}} args ap for fields, and the current project id.
 * @returns {IterableIterator<*|ForkEffect>}
 */
export function* fieldWatcherSaga(args = {}) {
  yield takeLatest(INITIALIZE_APP, fieldInitializerSaga, args);
}

function* fieldInitializerSaga(args) {
  try {
    yield put(load());
    const response = yield call(args.api.getAllMetadataFields, args.id);
    yield put({
      type: LOAD_SUCCESS,
      fields: response.data
    });
  } catch (error) {
    yield put({
      type: LOAD_ERROR,
      error
    });
  }
}

import { takeLatest, call, put } from "redux-saga/effects";
import { INITIALIZE_APP } from "../app/actions";
import {
  FIELD_API_CALL_REQUEST,
  FIELD_API_CALL_ERROR,
  FIELD_API_CALL_SUCCESS
} from "./constants";

/**
 * Watcher Saga: watches for actions dispatched to the store, and start the
 * appropriate saga.
 * @param {{api, id}} args ap for fields, and the current project id.
 * @returns {IterableIterator<*|ForkEffect>}
 */
export function* fieldWatcherSaga(args = {}) {
  yield takeLatest(INITIALIZE_APP, fieldWorkerSaga, args);
}

function* fieldWorkerSaga(args) {
  try {
    yield put({
      type: FIELD_API_CALL_REQUEST
    });
    const response = yield call(args.api.getAllMetadataFields, args.id);
    yield put({
      type: FIELD_API_CALL_SUCCESS,
      fields: response.data
    });
  } catch (error) {
    yield put({
      type: FIELD_API_CALL_ERROR,
      error
    });
  }
}

import { takeLatest, call, put } from "redux-saga/effects";
import {
  INITIALIZE_APP,
  FIELD_API_CALL_REQUEST,
  FIELD_API_CALL_SUCCESS,
  FIELD_API_CALL_ERROR
} from "../actions";
import fieldAPi from "./../api/metadataFieldApi";

/*
 * Watcher Saga: watches for actions dispatched to the store, and start the
 * appropriate saga.
 */
export function* fieldWatcherSaga() {
  yield takeLatest(INITIALIZE_APP, fieldWorkerSaga);
}

function* fieldWorkerSaga() {
  try {
    yield put({
      type: FIELD_API_CALL_REQUEST
    });
    const response = yield call(fieldAPi.getAllMetadataFields, 4);
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

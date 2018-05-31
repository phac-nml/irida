import { call, put, take } from "redux-saga/effects";
import { fetchMetadataEntries } from "../../../../apis/metadata/entry";
import { types } from "../../../../redux/reducers/app";
import { actions } from "../reducers/entries";

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  try {
    const { payload } = yield take(types.INIT_APP);
    yield put(actions.load());
    const { data: entries } = yield call(fetchMetadataEntries, payload.id);
    yield put(actions.success(entries));
  } catch (error) {
    yield put(actions.error(error));
  }
}

import { call, put, take } from "redux-saga/effects";
import {
  fetchMetadataEntries,
  saveMetadataEntryField
} from "../../../../apis/metadata/entry";
import { types as appTypes } from "../../../../redux/reducers/app";
import { types } from "../reducers/entries";
import { actions } from "../reducers/entries";

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  try {
    const { payload } = yield take(appTypes.INIT_APP);
    yield put(actions.load());
    const { data: entries } = yield call(fetchMetadataEntries, payload.id);
    yield put(actions.success(entries));
  } catch (error) {
    yield put(actions.error(error));
  }
}

export function* entryEditedSaga() {
  while (true) {
    const { entry, field } = yield take(types.EDITED);
    console.log(entry);
    yield call(saveMetadataEntryField, entry.sampleId, entry[field], field);
  }
}

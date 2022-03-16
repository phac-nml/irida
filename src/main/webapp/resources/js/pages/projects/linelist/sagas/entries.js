import { call, put, take } from "redux-saga/effects";
import {
  fetchMetadataEntries,
  saveMetadataEntryField,
} from "../../../../apis/metadata/entry";
import { types as appTypes } from "../../../../redux/reducers/app";
import { actions, types } from "../reducers/entries";
import { FIELDS } from "../constants";

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  let current = 0,
    pageSize = 5000,
    entries = [];
  try {
    const { payload } = yield take(appTypes.INIT_APP);
    yield put(actions.load());
    const pages = Math.ceil(window.PAGE.totalSamples / pageSize);

    for (let i = 0; i < pages; i++) {
      const { data } = yield call(fetchMetadataEntries, {
        projectId: payload.id,
        current: i,
        pageSize,
      });
      entries = [...entries, ...data.content];
      yield put(actions.loading(entries.length, data.total));
    }
    yield put(actions.success({ entries }));
  } catch (error) {
    yield put(actions.error(error));
  }
}

/**
 * Saga to handle updating the value of a metadata entry.
 * @returns {IterableIterator<*>}
 */
export function* entryEditedSaga() {
  // Always true, that way it can the listener is set up every time.
  while (true) {
    const { entry, label, field } = yield take(types.EDITED);
    yield call(
      saveMetadataEntryField,
      entry[FIELDS.sampleId],
      entry[field],
      label
    );
  }
}

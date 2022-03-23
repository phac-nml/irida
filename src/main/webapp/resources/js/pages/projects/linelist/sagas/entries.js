import { all, call, put, take } from "redux-saga/effects";
import {
  fetchMetadataEntries,
  saveMetadataEntryField,
} from "../../../../apis/metadata/entry";
import { actions, types } from "../reducers/entries";
import { FIELDS } from "../constants";

/**
 * Calculate the best page size for fetching entries
 * @returns size of page of entries to request
 */
function calculatePageSize() {
  const MIN = 1000;
  const MAX = 5000;
  const estimated = Math.ceil(window.PAGE.totalSamples / 100);
  return estimated < MIN ? MIN : estimated > MAX ? MAX : estimated;
}

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  let pageSize = calculatePageSize(),
    promises = [],
    count;
  try {
    const { payload } = yield take("METADATA_TEMPLATES_LOADED");
    yield put(actions.load());
    const pages = Math.ceil(window.PAGE.totalSamples / pageSize);

    for (let i = 0; i < pages; i++) {
      promises.push(call(fetchEntrySet, payload.id, i, pageSize));
    }
    const entries = yield all(promises);

    yield put(actions.success({ entries: entries.flat() }));
  } catch (error) {
    yield put(actions.error(error));
  }
}

/**
 * Get the entry set for a specified page size and
 * @param {number} projectId project identifier
 * @param {number} current page number
 * @param {number} pageSize size of the page to fetch
 * @returns entry set for the current page
 */
function* fetchEntrySet(projectId, current, pageSize) {
  const content = yield call(fetchMetadataEntries, {
    projectId,
    current,
    pageSize,
  });
  yield put(actions.loading(content.length, window.PAGE.totalSamples));
  return content;
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

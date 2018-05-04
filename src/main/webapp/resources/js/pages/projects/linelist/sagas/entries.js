import { call, put, take } from "redux-saga/effects";
import { fetchMetadataEntries } from "../../../../apis/metadata/entry";
import { types } from "../../../../redux/reducers/app";
import { actions } from "../reducers/entries";

/**
 * Format the row data.
 * Row should be {key: value}
 * @param {array} rows
 */
const formatRows = rows => {
  if (rows !== null) {
    return rows.map(r => {
      const row = {};
      Object.keys(r).forEach(item => {
        row[item] = r[item].value;
      });
      return row;
    });
  }
};

/**
 * Fetch all the metadata entries required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* entriesLoadingSaga() {
  try {
    const { id } = yield take(types.INIT_APP);
    yield put(actions.load());
    const { data } = yield call(fetchMetadataEntries, id);
    const entries = formatRows(data);
    yield put(actions.success(entries));
  } catch (error) {
    yield put(actions.error(error));
  }
}

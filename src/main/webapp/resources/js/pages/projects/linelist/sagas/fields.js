import { call, put, take } from "redux-saga/effects";
import { fetchMetadataFields } from "../../../../apis/metadata/field";
import { types } from "../../../../redux/reducers/app";
import { actions } from "../reducers/fields";

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

/**
 * Fetch all the metadata fields required to initialize the table.
 * @returns {IterableIterator<*>}
 */
export function* fieldsLoadingSaga() {
  try {
    // Waiting here for the app to be initialized.
    const { id } = yield take(types.INIT_APP);
    // Let the page know that we are loading the fields
    yield put(actions.load());
    // Wait for the API request to fetch the fields.
    const { data } = yield call(fetchMetadataFields, id);
    // Format the fields to work with ag-grid.
    const fields = formatColumns(data);
    // Let the application know that the fields are available.
    yield put(actions.success(fields));
  } catch (error) {
    yield put(actions.error(error));
  }
}

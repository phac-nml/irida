import { call, put, take } from "redux-saga/effects";
import { fetchMetadataFields } from "../../../../apis/metadata/field";
import { types } from "../../../../redux/reducers/app";
import { actions } from "../reducers/fields";

/*
Special handler for formatting the sample Name Column;
 */
const sampleNameColumn = {
  sort: "asc",
  pinned: "left",
  lockPosition: true,
  cellRenderer: "SampleNameRenderer"
};

/**
 * Format the column definitions.
 * @param {array} cols
 * @returns {*}
 */
const formatColumns = cols =>
  cols.map((f, i) => ({
    field: f.label,
    headerName: f.label.toUpperCase(),
    ...(i === 0 ? sampleNameColumn : {})
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

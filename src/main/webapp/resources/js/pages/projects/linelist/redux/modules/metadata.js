import { Map, List } from "immutable";
import { call, put } from "redux-saga/effects";

const LOAD = "linelist/metadata/LOAD_REQUEST";
const LOAD_ERROR = "linelist/metadata/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/metadata/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = Map({
  fetching: false, // Is the API call currently being made
  error: false, // Was there an error making the api call}
  fields: List(), // List of metadata fields ==> used for table headers
  entries: List() // list of metadata entries ==> table content.
});

/*
REDUCERS - Handle updating the state based on the action that is passed.
This is the **ONLY** place the updates can be made to the state of metadata
fields and entries.
 */
export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD:
      return state.set("fetching", true).set("error", false);
    case LOAD_SUCCESS:
      return state
        .set("fetching", false)
        .set("error", false)
        .set("fields", action.fields)
        .set("entries", action.entries);
    case LOAD_ERROR:
      return state
        .set("fetching", false)
        .set("error", false)
        .set("fields", List())
        .set("entries", List());
    default:
      return state;
  }
}

/*
ACTIONS
 */
const load = () => ({ type: LOAD });

const loadSuccess = ({ fields, entries }) => ({
  type: LOAD_SUCCESS,
  fields,
  entries
});

const loadError = error => ({
  type: LOAD_ERROR,
  error
});

/*
SAGAS
 */

/**
 * Fetch all the metadata fields and entries required to initialize the table.
 * @param {function} fetchFields api to fetch metadata fields for the project
 * @param {function} fetchEntries api to fetch metadata entries for the project.
 * @param {Number} projectId project identifier
 * @returns {IterableIterator<*>}
 */
export function* metadataLoadingSaga(fetchFields, fetchEntries, projectId) {
  try {
    yield put(load());
    const { data: fields } = yield call(fetchFields, projectId);
    const { data: entries } = yield call(fetchEntries, projectId);
    yield put(
      loadSuccess({ fields: List.of(fields), entries: List.of(entries) })
    );
  } catch (error) {
    yield put(loadError(error));
  }
}

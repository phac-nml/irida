import { call, put } from "redux-saga/effects";

const LOAD = "linelist/metadata/LOAD_REQUEST";
const LOAD_ERROR = "linelist/metadata/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/metadata/LOAD_SUCCESS";

/*
INITIAL STATE
 */
const initialState = {
  fetching: false, // Is the API call currently being made
  error: null, // Was there an error making the api call}
  fields: null, // List of metadata fields ==> used for table headers
  entries: null // list of metadata entries ==> table content.
};

/*
REDUCERS - Handle updating the state based on the action that is passed.
This is the **ONLY** place the updates can be made to the state of metadata
fields and entries.
 */
export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD:
      return { ...state, fetching: true, error: null };
    case LOAD_SUCCESS:
      return {
        ...state,
        fetching: false,
        error: false,
        fields: action.fields,
        entries: action.entries
      };
    case LOAD_ERROR:
      return {
        ...state,
        fetching: false,
        error: true,
        fields: null,
        entries: null
      };
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
    yield put(loadSuccess({ fields, entries }));
  } catch (error) {
    yield put(loadError(error));
  }
}

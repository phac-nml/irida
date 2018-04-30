import { call, put, take, takeLatest } from "redux-saga/effects";
import { fetchTemplates } from "../../apis";
import { INIT_APP } from "./app";

const LOAD_TEMPLATES = "linelist/templates/LOAD_REQUEST";
const LOAD_TEMPLATES_ERROR = "linelist/templates/LOAD_TEMPLATES_ERROR";
const LOAD_TEMPLATES_SUCCESS = "linelist/templates/LOAD_TEMPLATES_SUCCESS";

const initialState = {
  fetching: false,
  error: null,
  templates: []
};

export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD_TEMPLATES:
      return { ...state, fetching: true, error: null };
    case LOAD_TEMPLATES_SUCCESS:
      return {
        ...state,
        fetching: false,
        templates: action.templates
      };
    case LOAD_TEMPLATES_ERROR:
      return { ...state, fetching: false, error: action.error };

    default:
      return state;
  }
}

// LOCAL ACTIONS
const load = () => ({ type: LOAD_TEMPLATES });
const loadSuccess = templates => ({ type: LOAD_TEMPLATES_SUCCESS, templates });
const loadError = error => ({ type: LOAD_TEMPLATES_ERROR, error });

/**
 * Initialize templates in the line list
 * @returns {IterableIterator<*>}
 */
export function* templatesLoadingSaga() {
  try {
    const { id } = yield take(INIT_APP);
    yield put(load());
    const { data: templates } = yield call(fetchTemplates, id);
    yield put(loadSuccess(templates));
  } catch (error) {
    yield put(loadError(error));
  }
}

export function* validateTemplateNameSaga() {
  yield takeLatest(VALIDATE_TEMPLATE_NAME, validateNameSaga);
}

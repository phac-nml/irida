import { call, put, take, takeLatest } from "redux-saga/effects";
import { fetchTemplates, fetchTemplate } from "../../apis";
import { INIT_APP } from "./app";

const LOAD_TEMPLATES = "linelist/templates/LOAD_REQUEST";
const LOAD_TEMPLATES_ERROR = "linelist/templates/LOAD_TEMPLATES_ERROR";
const LOAD_TEMPLATES_SUCCESS = "linelist/templates/LOAD_TEMPLATES_SUCCESS";

/*
SPECIFIC TEMPLATES
 */
const FETCH_TEMPLATE = "linelist/templates/FETCH_TEMPLATE";
const TEMPLATE_FETCH_SUCCESS = "linelist/templates/TEMPLATE_FETCH_SUCCESS";

const initialState = {
  fetching: false,
  error: null,
  templates: [],
  current: -1,
  loadingTemplate: false,
  template: []
};

export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case LOAD_TEMPLATES:
      return { ...state, fetching: true, error: null };
    case LOAD_TEMPLATES_SUCCESS:
      return { ...state, fetching: false, templates: action.templates };
    case LOAD_TEMPLATES_ERROR:
      return { ...state, fetching: false, error: action.error };
    case FETCH_TEMPLATE:
      return { ...state, current: action.id, loadingTemplate: true };
    case TEMPLATE_FETCH_SUCCESS:
      return { ...state, loadingTemplate: false, template: action.template };
    default:
      return state;
  }
}

// LOCAL ACTIONS
const load = () => ({ type: LOAD_TEMPLATES });
const loadSuccess = templates => ({ type: LOAD_TEMPLATES_SUCCESS, templates });
const loadError = error => ({ type: LOAD_TEMPLATES_ERROR, error });
const fetchTemplateSuccess = template => ({
  type: TEMPLATE_FETCH_SUCCESS,
  template
});

// GLOBAL ACTIONS
export const actions = {
  fetchTemplate: id => ({ type: FETCH_TEMPLATE, id })
};

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

/**
 * Listen for a change in templates.
 * @returns {IterableIterator<ForkEffect | *>}
 */
export function* watchFetchTemplateSaga() {
  yield takeLatest(FETCH_TEMPLATE, loadTemplateSaga);
}

export function* loadTemplateSaga(action) {
  try {
    const id = Number(action.id);
    if (-1 < id) {
      const { data: template } = yield call(fetchTemplate, id);
      yield put(fetchTemplateSuccess(template));
    } else {
      yield put(fetchTemplateSuccess([]));
    }
  } catch (error) {
    // TODO: (Josh | 2018-04-19) CATCH THIS
  }
}

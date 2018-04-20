import { call, put, take, takeLatest } from "redux-saga/effects";
import { fetchTemplates, fetchTemplate } from "../../apis";
import { INIT_APP } from "./app";

const LOAD = "linelist/templates/LOAD_REQUEST";
const LOAD_ERROR = "linelist/templates/LOAD_ERROR";
const LOAD_SUCCESS = "linelist/templates/LOAD_SUCCESS";
const USE_TEMPLATE = "linelist/templates/USE_TEMPLATE";
const LOAD_TEMPLATE = "linelist/templates/LOAD_TEMPLATE";
const TEMPLATE_LOADED = "linelist/templates/TEMPLATE_LOADED";

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
    case LOAD:
      return { ...state, fetching: true, error: null };
    case LOAD_SUCCESS:
      return { ...state, fetching: false, templates: action.templates };
    case LOAD_ERROR:
      return { ...state, fetching: false, error: action.error };
    case USE_TEMPLATE:
      return { ...state, current: action.id };
    case LOAD_TEMPLATE:
      return { ...state, loadingTemplate: true };
    case TEMPLATE_LOADED:
      return {
        ...state,
        loadingTemplate: false,
        template: action.template,
        id: action.id
      };
    default:
      return state;
  }
}

// LOCAL ACTIONS
const load = () => ({ type: LOAD });
const loadSuccess = templates => ({ type: LOAD_SUCCESS, templates });
const loadError = error => ({ type: LOAD_ERROR, error });
const loadTemplate = () => ({ type: LOAD_TEMPLATE });
const templateLoaded = (template, id) => ({
  type: TEMPLATE_LOADED,
  template,
  id
});

// GLOBAL ACTIONS
export const actions = {
  useTemplate: id => ({ type: USE_TEMPLATE, id })
};

export function* templateLoadingSaga(id) {
  try {
    const { id } = yield take(INIT_APP);
    yield put(load());
    const { data: templates } = yield call(fetchTemplates, id);
    yield put(loadSuccess(templates));

    /*
    Listen for a change in templates.
     */
    yield takeLatest(USE_TEMPLATE, loadTemplateSaga);
  } catch (error) {
    yield put(loadError(error));
  }
}

export function* loadTemplateSaga(action) {
  try {
    const id = Number(action.id);
    if (-1 < id) {
      yield put(loadTemplate());
      const { data: template } = yield call(fetchTemplate, id);
      yield put(templateLoaded(template, id));
    } else {
      yield put(templateLoaded([], id));
    }
  } catch (error) {
    // TODO: (Josh | 2018-04-19) CATCH THIS
  }
}

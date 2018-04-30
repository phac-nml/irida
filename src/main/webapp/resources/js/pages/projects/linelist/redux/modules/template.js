import { call, put, take, takeLatest } from "redux-saga/effects";
import { fetchTemplate } from "../../apis";

const FETCH_TEMPLATE = "linelist/template/FETCH_TEMPLATE";
const TEMPLATE_MODIFIED = "linelist/template/TEMPLATE_MODIFIED";
export const TEMPLATE_FETCH_SUCCESS =
  "linelist/template/TEMPLATE_FETCH_SUCCESS";
const VALIDATE_TEMPLATE_NAME = "linelist/template/VALIDATE_TEMPLATE_NAME";

const initialState = {
  id: 0,
  modified: false,
  loading: false,
  fields: [],
  validating: false
};

export function reducer(state = initialState, action = {}) {
  switch (action.type) {
    case FETCH_TEMPLATE:
      return {
        ...state,
        id: action.id,
        loading: true
      };
    case TEMPLATE_FETCH_SUCCESS:
      return {
        ...state,
        loading: false,
        fields: action.fields,
        modified: false
      };
    case TEMPLATE_MODIFIED:
      return {
        ...state,
        id: -2,
        modified: true
      };
    case VALIDATE_TEMPLATE_NAME:
      return { ...state, validating: true };
    default:
      return state;
  }
}

/*
ACTIONS
 */
const fetchTemplateSuccess = fields => ({
  type: TEMPLATE_FETCH_SUCCESS,
  fields
});

// GLOBAL ACTIONS
export const actions = {
  fetchTemplate: id => ({ type: FETCH_TEMPLATE, id }),
  templateModified: () => ({ type: TEMPLATE_MODIFIED }),
  validateTemplateName: name => ({ type: VALIDATE_TEMPLATE_NAME, name })
};

/**
 * Listen for a change in templates.
 * @returns {IterableIterator<ForkEffect | *>}
 */
export function* fetchTemplateSaga() {
  yield takeLatest(FETCH_TEMPLATE, loadTemplate);
}

export function* loadTemplate(action) {
  try {
    const id = Number(action.id);
    if (0 < id) {
      const { data: fields } = yield call(fetchTemplate, id);
      yield put(fetchTemplateSuccess(fields));
    } else {
      yield put(fetchTemplateSuccess([]));
    }
  } catch (error) {
    // TODO: (Josh | 2018-0409) CATCH THIS
  }
}

function* validateNameSaga(action) {
  console.log(action);
}

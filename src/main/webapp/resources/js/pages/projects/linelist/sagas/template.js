import { call, put, takeLatest } from "redux-saga/effects";
import { actions, NO_TEMPLATE_INDEX, types } from "../reducers/template";
import { fetchTemplate } from "../../../../apis/metadata/templates";

/**
 * Listen for a change in templates.
 * @returns {IterableIterator<ForkEffect | *>}
 */
export function* watchFetchTemplateSaga() {
  yield takeLatest(types.LOAD, loadTemplateSaga);
}

export function* loadTemplateSaga(action) {
  try {
    const id = Number(action.id);
    if (NO_TEMPLATE_INDEX === id) {
      yield put(actions.success([], id));
    } else {
      const { data: template } = yield call(fetchTemplate, id);
      yield put(actions.success(template, id));
    }
  } catch (error) {
    // TODO: (Josh | 2018-04-19) CATCH THIS
  }
}

export function* validateTemplateNameSaga() {
  yield takeLatest(types.VALIDATE_TEMPLATE_NAME, validateTemplateName);
}

function* validateTemplateName(action) {
  const { name } = action;

  console.log(name);
  console.info("NEED TO IMPLEMENT THIS");
}

import { call, put, take } from "redux-saga/effects";
import { types } from "../../../../redux/reducers/app";
import { actions } from "../reducers/templates";
import { fetchTemplates } from "../../../../apis/metadata/templates";

/**
 * Initialize templates in the line list
 * @returns {IterableIterator<*>}
 */
export function* templatesLoadingSaga() {
  try {
    const { id } = yield take(types.INIT_APP);
    yield put(load());
    const { data: templates } = yield call(fetchTemplates, id);
    yield put(actions.success(templates));
  } catch (error) {
    yield put(actions.error(error));
  }
}
//
// /**
//  * Listen for a change in templates.
//  * @returns {IterableIterator<ForkEffect | *>}
//  */
// export function* watchFetchTemplateSaga() {
//   yield takeLatest(FETCH_TEMPLATE, loadTemplateSaga);
// }
//
// export function* loadTemplateSaga(action) {
//   try {
//     const id = Number(action.id);
//     if (-1 < id) {
//       const { data: template } = yield call(fetchTemplate, id);
//       yield put(fetchTemplateSuccess(template));
//     } else {
//       yield put(fetchTemplateSuccess([]));
//     }
//   } catch (error) {
//     // TODO: (Josh | 2018-04-19) CATCH THIS
//   }
// }

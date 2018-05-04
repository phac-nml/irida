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
    yield put(actions.load());
    const { data: templates } = yield call(fetchTemplates, id);
    yield put(actions.success(templates));
  } catch (error) {
    yield put(actions.error(error));
  }
}

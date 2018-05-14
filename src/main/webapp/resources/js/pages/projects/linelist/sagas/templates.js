import { call, put, take } from "redux-saga/effects";
import { types as appTypes } from "../../../../redux/reducers/app";
import { actions, types } from "../reducers/templates";
import {
  fetchTemplates,
  saveTemplate
} from "../../../../apis/metadata/templates";

/**
 * Initialize templates in the line list
 * @returns {IterableIterator<*>}
 */
export function* templatesLoadingSaga() {
  try {
    const { payload } = yield take(appTypes.INIT_APP);
    yield put(actions.load());
    const { data: templates } = yield call(fetchTemplates, payload.id);
    yield put(actions.success(templates));
  } catch (error) {
    yield put(actions.error(error));
  }
}

export function* saveTemplateSaga() {
  while (true) {
    try {
      const { data } = yield take(types.SAVE_TEMPLATE);
      console.log(data);
      yield call(saveTemplate, data);
    } catch (error) {
      // TODO: (Josh | 2018-05-14) Handle this in the UI
      console.error("ERROR SAVING TEMPLATE", error);
    }
  }
}

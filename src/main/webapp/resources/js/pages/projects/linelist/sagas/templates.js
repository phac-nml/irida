import { call, delay, put, take } from "redux-saga/effects";
import { types as appTypes } from "../../../../redux/reducers/app";
import { actions, types } from "../reducers/templates";
import {
  fetchTemplates,
  saveTemplate
} from "../../../../apis/metadata/templates";
import { FIELDS } from "../constants";

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

      /*
      Remove hidden fields and the sample label or any icons
      */
      data.fields = data.fields.filter(
        f =>
          !f.hide && f.field !== FIELDS.sampleName && f.field !== FIELDS.icons
      );

      /*
      Post to new template to the server
       */
      const { data: response } = yield call(saveTemplate, data);
      yield put(actions.savedTemplate(response.UIMetadataTemplate));
      // Delay allows for displaying the saved message
      yield delay(2500);
      yield put(actions.savedComplete());
    } catch (error) {
      // TODO: (Josh | 2018-05-14) Handle this in the UI
      console.error("ERROR SAVING TEMPLATE", error);
    }
  }
}

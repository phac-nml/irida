import { call, put, take } from "redux-saga/effects";
import { actions, types } from "../reducers/cart";
import { putSampleInCart } from "../../apis/cart/cart";
import { FIELDS } from "../../pages/projects/linelist/constants";

/**
 * Saga to add samples to the global cart.
 * @returns {IterableIterator<*>}
 */
export function* addToCartSaga() {
  while (true) {
    const { samples } = yield take(types.ADD);
    if (samples.length > 0) {
      const projectId = samples[0][FIELDS.projectId];
      const sampleIds = samples.map(s => ({
        id: s[FIELDS.sampleId],
        label: s[FIELDS.sampleName]
      }));

      const { data } = yield call(putSampleInCart, projectId, sampleIds);

      if (data.count) {
        yield put(actions.updated(data));
      }
    }
  }
}

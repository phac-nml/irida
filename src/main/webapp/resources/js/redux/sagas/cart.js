import { call, put, take } from "redux-saga/effects";
import { getCartCount, putSampleInCart } from "../../apis/cart/cart";
import { FIELDS } from "../../pages/projects/linelist/constants";
import { types as appTypes } from "../reducers/app";
import { actions, types } from "../reducers/cart";

/**
 * Saga to initialize the number of samples in the cart.
 * @returns {IterableIterator<*>}
 */
export function* initializeCartPage() {
  yield take(appTypes.INIT_APP);
  const count = yield call(getCartCount);
  yield put(actions.initialized(count));
}

/**
 * Saga to add samples to the global cart.
 * @returns {IterableIterator<*>}
 */
export function* addToCartSaga() {
  while (true) {
    const { samples } = yield take(types.ADD);
    if (samples.length > 0) {
      const projectId = samples[0][FIELDS.projectId];
      const sampleIds = samples.map((s) => ({
        id: s[FIELDS.sampleId],
        label: s[FIELDS.sampleName],
      }));

      const count = yield call(putSampleInCart, projectId, sampleIds);

      if (count) {
        yield put(actions.updated({ count }));
      }
    }
  }
}

import { call, put, take } from "redux-saga/effects";
import { actions, types } from "../reducers/cart";
import { types as appTypes } from "../reducers/app";
import {
  emptyCart,
  getCartCount,
  putSampleInCart,
  removeSample,
  removeProject,
  getCartIds,
  getSamplesForProjects
} from "../../apis/cart/cart";
import { FIELDS } from "../../pages/projects/linelist/constants";

/**
 * Saga to initialize the number of samples in the cart.
 * @returns {IterableIterator<*>}
 */
export function* initializeCartPage() {
  yield take(appTypes.INIT_APP);
  const { count } = yield call(getCartCount);
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

/**
 * Remove all samples from the cart.
 * @returns {IterableIterator<*>}
 */
export function* empty() {
  yield take(types.CART_EMPTY);
  yield call(emptyCart);
  yield put(actions.updated({ count: 0 }));
}

/**
 * Remove a simple sample from the cart
 * @returns {IterableIterator<*>}
 */
export function* removeSampleFromCart() {
  while (true) {
    const { payload } = yield take(types.REMOVE_SAMPLE);
    const { count } = yield call(
      removeSample,
      payload.projectId,
      payload.sampleId
    );
    yield put(actions.updated({ count }));
  }
}

/**
 * Remove an entire project from the cart.
 * @returns {IterableIterator<PutEffect<*|{payload, type}>|CallEffect|TakeEffect>}
 */
export function* removeProjectFromCart() {
  while (true) {
    const { payload } = yield take(types.REMOVE_PROJECT);
    const { count } = yield call(removeProject, payload.id);
    yield put(actions.updated({ count }));
  }
}

export function* loadFullCart() {
  yield take(types.LOAD_CART);
  const { ids } = yield call(getCartIds);
  const samples = yield call(getSamplesForProjects, ids);
  yield put(actions.cartLoaded(samples));
}

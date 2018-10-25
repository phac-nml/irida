import { call, put, take } from "redux-saga/effects";
import { actions, types } from "../reducers/cart";
import { putSampleInCart } from "../../apis/cart/cart";

/**
 * Saga to add samples to the global cart.
 * @returns {IterableIterator<*>}
 */
export function* addToCartSaga() {
  while (true) {
    const { samples } = yield take(types.ADD);
    if (samples.length > 0) {
      const projectId = samples[0].projectId;
      const sampleIds = samples.map(s => ({
        id: s.sampleId,
        label: s.sampleLabel
      }));

      const { data } = yield call(putSampleInCart, projectId, sampleIds);

      if (data.count) {
        yield put(actions.updated(data));
      }

      // // This is a hack until the cart gets fully updated
      // const event = new Event("cart:updated");
      // document.dispatchEvent(event);
      // // Display the notification.
      // showNotification({ text: data.message });
    }
  }
}

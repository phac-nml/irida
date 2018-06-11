import { call, put, take } from "redux-saga/effects";
import { types } from "../reducers/cart";
import { putSampleInCart } from "../../apis/cart/cart";
import { showNotification } from "../../modules/notifications";

/**
 * Saga to add samples to the global cart.
 * @returns {IterableIterator<*>}
 */
export function* addToCartSaga() {
  try {
    while (true) {
      const { samples } = yield take(types.ADD);
      if (samples.length > 0) {
        const projectId = samples[0].projectId;
        const sampleIds = samples.map(s => s.sampleId);
        const { data } = yield call(putSampleInCart, projectId, sampleIds);
        // This is a hack until the cart gets fully updated
        const event = new Event("cart:updated");
        document.dispatchEvent(event);
        showNotification({ text: data.message });
      }
    }
  } catch (error) {
    console.error("COULD NOT ADD TO CART", error);
  }
}

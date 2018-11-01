import { call, take } from "redux-saga/effects";
import { types } from "../reducers/cart";
import { putSampleInCart } from "../../apis/cart/cart";
import { showNotification } from "../../modules/notifications";
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
      const sampleIds = samples.map(s => s[FIELDS.sampleId]);
      const { data } = yield call(putSampleInCart, projectId, sampleIds);
      // This is a hack until the cart gets fully updated
      const event = new Event("cart:updated");
      document.dispatchEvent(event);
      // Display the notification.
      showNotification({ text: data.message });
    }
  }
}

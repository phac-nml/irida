import { call, put, take } from "redux-saga/effects";
import { types } from "../reducers/cart";
import { putSampleInCart } from "../../apis/cart/cart";

export function* addToCartSaga() {
  try {
    while (true) {
      const { samples } = yield take(types.ADD);
      if (samples.length > 0) {
        const projectId = samples[0].projectId;
        const sampleIds = samples.map(s => s.sampleId);
        yield call(putSampleInCart, projectId, sampleIds);
      }
    }
  } catch (error) {
    console.error("COULD NOT ADD TO CART", error);
  }
}

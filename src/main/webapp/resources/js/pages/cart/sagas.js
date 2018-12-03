import { call, put, take } from "redux-saga/effects";
import { TYPES, actions } from "./reducer";
import { getCartCount } from "../../apis/cart/cart";

export function* initializeCartPage() {
  try {
    yield take(TYPES.CART_INITIALIZE);
    const { count } = yield call(getCartCount);
    yield put(actions.initialized(count));
  } catch (e) {
    console.log("LOADING TOTAL ERROR", e);
  }
}

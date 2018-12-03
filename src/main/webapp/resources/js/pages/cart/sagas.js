import { call, put, take } from "redux-saga/effects";
import { TYPES, actions } from "./reducer";
import { types as cartTypes } from "../../redux/reducers/cart";
import { getCartIds } from "../../apis/cart/cart";

export function* getCarProjectIds() {
  const { count } = yield take(cartTypes.INITIALIZED);
  if (count > 0) {
    const { ids } = yield call(getCartIds);
    for (let id of ids) {
      console.log(id);
      yield call(getCartIds);
    }
  }
}

import { call, put, take } from "redux-saga/effects";
import { TYPES, actions } from "./reducer";
import {
  types as globalCartTypes,
  actions as globalCartActions
} from "../../redux/reducers/cart";
import {
  getCartIds,
  emptyCart,
  getSamplesForProject
} from "../../apis/cart/cart";

export function* getCartProjectIds() {
  const { count } = yield take(globalCartTypes.INITIALIZED);
  if (count > 0) {
    const { ids } = yield call(getCartIds);
    for (let id of ids) {
      const samples = yield call(getSamplesForProject, id);
      yield put(actions.samplesLoaded(samples));
    }
  }
}

export function* empty() {
  yield take(TYPES.CART_EMPTY);
  yield call(emptyCart);
  yield put(globalCartActions.updated({ count: 0 }));
}

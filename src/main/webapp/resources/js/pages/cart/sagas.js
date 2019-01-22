import { call, put, take } from "redux-saga/effects";
import { cartPageActions, cartPageTypes } from "./reducer";
import {
  actions as globalCartActions,
  types as globalCartTypes
} from "../../redux/reducers/cart";
import {
  emptyCart,
  getCartIds,
  getSamplesForProject,
  removeSample
} from "../../apis/cart/cart";

export function* getCartProjectIds() {
  const { count } = yield take(globalCartTypes.INITIALIZED);
  if (count > 0) {
    const { ids } = yield call(getCartIds);
    for (let id of ids) {
      const samples = yield call(getSamplesForProject, id);
      yield put(cartPageActions.samplesLoaded(samples));
    }
  }
}

export function* empty() {
  yield take(cartPageTypes.CART_EMPTY);
  yield call(emptyCart);
  yield put(globalCartActions.updated({ count: 0 }));
}

export function* removeSampleFromCart() {
  while (true) {
    const { sampleId, projectId } = yield take(cartPageTypes.REMOVE_SAMPLE);
    yield call(removeSample, projectId, sampleId);
    yield put(cartPageActions.sampleRemovedSuccess(sampleId));
  }
}

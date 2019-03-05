import { all, call, delay, put, take } from "redux-saga/effects";
import { actions, types } from "./reducer";
import {
  getGalaxyClientAuthentication,
  getGalaxySamples
} from "../../apis/galaxy/galaxy";
import { authenticateOauthClient } from "../../apis/oauth/oauth";

export function* getCartGalaxySamplesSaga() {
  yield take(types.GET_GALAXY_SAMPLES);
  const samples = yield call(getGalaxySamples);
  yield delay(1500); // Short wait so that the UI does not flash instantly.
  yield put(actions.setGalaxySamples(samples));
}

export function* checkOauthStatusSaga() {
  while (true) {
    yield take(types.CHECK_OAUTH);
    const { authenticated } = yield call(
      getGalaxyClientAuthentication,
      window.GALAXY.CLIENT_ID
    );

    if (authenticated) {
      yield put(actions.oauthComplete());
    }
    yield put(actions.setOathValidationStatus(authenticated));
  }
}

export function* validateOauthClientSaga() {
  const redirect = `${window.TL.BASE_URL}galaxy/auth_code`;
  while (true) {
    yield take(types.AUTHENTICATE_OATH);
    const code = yield call(
      authenticateOauthClient,
      window.GALAXY.CLIENT_ID,
      redirect
    );
    if (typeof code === "undefined") {
      yield put(actions.oauthError());
    } else {
      yield put(actions.oauthSuccess(code, redirect));
      yield put(actions.oauthComplete());
    }
  }
}

export function* submitGalaxyDataSaga() {
  yield all([take(types.SET_GALAXY_SAMPLES), take(types.OAUTH_COMPLETE)]);
  yield put(actions.enableSubmit());
}

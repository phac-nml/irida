import { call, delay, put, take } from "redux-saga/effects";
import { actions, types } from "./reducer";
import { types as cartTypes } from "../../redux/reducers/cart";
import {
  getGalaxySamples,
  removeGalaxySession
} from "../../apis/galaxy/galaxy";
import { authenticateOauthClient } from "../../apis/oauth/oauth";
import { exportToGalaxy } from "../../apis/galaxy/submission";

async function validateOauthClient() {
  const redirect = `${window.TL.BASE_URL}galaxy/auth_code`;
  return authenticateOauthClient(window.GALAXY.CLIENT_ID, redirect)
    .then(code => code)
    .catch(response => response);
}

export function* getCartGalaxySamplesSaga() {
  while (true) {
    yield take(types.GET_GALAXY_SAMPLES);
    console.log("GETTING SAMPLES");
    const samples = yield call(getGalaxySamples);
    yield delay(1500); // Short wait so that the UI does not flash instantly.
    yield put(actions.setGalaxySamples(samples));
  }
}

export function* submitGalaxyDataSaga() {
  while (true) {
    // 1. Wait for the submit call
    const {
      payload: { email, makepairedcollection, samples }
    } = yield take(types.SUBMIT);

    // 2. Get galaxy Oauth2 code
    // result code be the code, or constant 'ERROR' for authentication error or 'CLOSED' for window closed
    const result = yield call(validateOauthClient);

    if (result === "ERROR") {
      yield put(actions.submitError());
    } else if (result === "CLOSED") {
      yield put(actions.oauthWindowClosed());
    } else {
      // Everything checks out.
      // Remove the galaxy session from IRIDA and submit to galaxy.
      yield call(removeGalaxySession);
      exportToGalaxy(
        email,
        makepairedcollection,
        result,
        `${window.TL.BASE_URL}galaxy/auth_code`,
        samples
      );
    }
  }
}

export function* samplesUpdated() {
  while (true) {
    yield take(cartTypes.UPDATED);
    yield put(actions.getGalaxySamples());
  }
}

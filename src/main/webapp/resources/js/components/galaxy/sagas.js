import { call, delay, put, take } from "redux-saga/effects";
import { actions, types } from "./reducer";
import { getGalaxySamples } from "../../apis/galaxy/galaxy";
import { authenticateOauthClient } from "../../apis/oauth/oauth";
import { exportToGalaxy } from "../../apis/galaxy/submission";

export function* getCartGalaxySamplesSaga() {
  yield take(types.GET_GALAXY_SAMPLES);
  const samples = yield call(getGalaxySamples);
  yield delay(1500); // Short wait so that the UI does not flash instantly.
  yield put(actions.setGalaxySamples(samples));
}

async function validateOauthClient() {
  const redirect = `${window.TL.BASE_URL}galaxy/auth_code`;
  return authenticateOauthClient(window.PAGE.galaxyClientID, redirect)
    .then(code => code)
    .then(response => response);
}

export function* submitGalaxyDataSaga() {
  // 1. Wait for the submit call
  const {
    payload: { email, makepairedcollection, samples }
  } = yield take(types.SUBMIT);

  // 2. Get galaxy Oauth2 code
  // result code be the code, or constant 'ERROR' for authentication error or 'CLOSED' for window closed
  const result = yield call(validateOauthClient);
  if (result === "ERROR") {
    console.log("ERROR");
  } else if (result === "CLOSED") {
    console.log("CLOSED");
  } else {
    // We have the code!
    exportToGalaxy(
      email,
      makepairedcollection,
      result,
      `${window.TL.BASE_URL}galaxy/auth_code`,
      samples
    );
  }
}

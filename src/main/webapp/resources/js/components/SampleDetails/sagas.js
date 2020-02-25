import { put, take } from "redux-saga/effects";
import { sampleDetailsActions, sampleDetailsTypes } from "./reducer";
import { getSampleDetails } from "../../apis/samples/samples";

export function* getDetailsForSample() {
  while (true) {
    const { sample: s } = yield take(sampleDetailsTypes.DISPLAY_DETAILS);
    const { sample, metadata, modifiable } = yield getSampleDetails(s.id);
    yield put(sampleDetailsActions.sampleLoaded(sample, metadata, modifiable));
  }
}

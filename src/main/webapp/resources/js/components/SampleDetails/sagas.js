import { take, put } from "redux-saga/effects";
import { sampleDetailsTypes, sampleDetailsActions } from "./reducer";
import { getSampleDetails } from "../../apis/samples/samples";

export function* getDetailsForSample() {
  while (true) {
    const { sample: s } = yield take(sampleDetailsTypes.DISPLAY_DETAILS);
    const { sample, metadata, modifiable } = yield getSampleDetails(s.id);
    yield put(sampleDetailsActions.sampleLoaded(sample, metadata, modifiable));
  }
}

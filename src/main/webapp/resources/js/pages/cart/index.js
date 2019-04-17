import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getStore } from "../../redux/getStore";
import {
  getDetailsForSample,
  sampleDetailsReducer
} from "../../components/SampleDetails";
import { actions } from "../../redux/reducers/app";
import { reducer as galaxyReducer } from "../../components/galaxy/reducer";
import {
  empty,
  removeProjectFromCart,
  removeSampleFromCart,
  loadFullCart
} from "../../redux/sagas/cart";
import {
  getCartGalaxySamplesSaga,
  samplesUpdated,
  submitGalaxyDataSaga
} from "../../components/galaxy/sagas";
import { Cart } from "./components/Cart";

const store = getStore(
  { sampleDetailsReducer, galaxyReducer },
  {
    empty,
    getDetailsForSample,
    removeSampleFromCart,
    removeProjectFromCart,
    loadFullCart,
    getCartGalaxySamplesSaga,
    submitGalaxyDataSaga,
    samplesUpdated
  }
);

render(
  <Provider store={store}>
    <Cart />
  </Provider>,
  document.querySelector("#root")
);

store.dispatch(actions.initialize({}));

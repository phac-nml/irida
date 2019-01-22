import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getStore } from "../../redux/getStore";
import {
  getDetailsForSample,
  sampleDetailsReducer
} from "../../components/SampleDetails";
import { actions } from "../../redux/reducers/app";
import { empty, removeSampleFromCart } from "../../redux/sagas/cart";
import CartPage from "./components/CartPage";

const store = getStore(
  { sampleDetailsReducer },
  { empty, getDetailsForSample, removeSampleFromCart }
);

render(
  <Provider store={store}>
    <CartPage />
  </Provider>,
  document.querySelector("#root")
);

store.dispatch(actions.initialize({}));

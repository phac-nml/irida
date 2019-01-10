import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getStore } from "../../redux/getStore";
import { cartPageReducer } from "./reducer";
import {
  sampleDetailsReducer,
  getDetailsForSample
} from "../../components/SampleDetails";
import { actions } from "../../redux/reducers/app";
import { getCartProjectIds, empty } from "./sagas";
import CartPage from "./components/CartPage";

const store = getStore(
  { cartPageReducer, sampleDetailsReducer },
  { getCartProjectIds, empty, getDetailsForSample }
);

render(
  <Provider store={store}>
    <CartPage />
  </Provider>,
  document.querySelector("#root")
);

store.dispatch(actions.initialize({}));

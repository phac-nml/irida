import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getStore } from "../../redux/getStore";
import {
  getDetailsForSample,
  sampleDetailsReducer
} from "../../components/SampleDetails";
import { actions } from "../../redux/reducers/app";
import {
  empty,
  loadFullCart,
  removeProjectFromCart,
  removeSampleFromCart
} from "../../redux/sagas/cart";
import { Cart } from "./components/Cart";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = `${window.TL.BASE_URL}resources/dist/`;

const store = getStore(
  { sampleDetailsReducer },
  {
    empty,
    getDetailsForSample,
    removeSampleFromCart,
    removeProjectFromCart,
    loadFullCart
  }
);

render(
  <Provider store={store}>
    <Cart />
  </Provider>,
  document.querySelector("#root")
);

store.dispatch(actions.initialize({}));

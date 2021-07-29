import { configureStore } from "@reduxjs/toolkit";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { cartApi } from "../../apis/cart/cart";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Cart } from "./components/Cart";

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`dist/`);

const store = configureStore({
  reducer: {
    [cartApi.reducerPath]: cartApi.reducer,
  },
});

render(
  <Provider store={store}>
    <Cart />
  </Provider>,
  document.querySelector("#root")
);

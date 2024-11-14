import { configureStore } from "@reduxjs/toolkit";
import React from "react";
import { createRoot } from 'react-dom/client';
import { Provider } from "react-redux";
import { cartApi } from "../../apis/cart/cart";
import { projectsApi } from "../../apis/projects/projects";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Cart } from "./components/Cart";

/**
 * @fileoverview This is the entry file for the Cart Page.  It uses a redux store,
 * configured using the redux toolkit.
 */

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
    [projectsApi.reducerPath]: projectsApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(cartApi.middleware, projectsApi.middleware),
});

const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <Provider store={store}>
    <Cart />
  </Provider>
);

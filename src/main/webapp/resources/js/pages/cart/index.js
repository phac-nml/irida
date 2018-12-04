import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { getStore } from "../../redux/getStore";
import { reducer as cartPageReducer } from "./reducer";
import { actions } from "../../redux/reducers/app";
import { getCartProjectIds } from "./sagas";
import CartPage from "./components/CartPage";

const store = getStore(
  { cartPageReducer },
  { getCarProjectIds: getCartProjectIds }
);

render(
  <Provider store={store}>
    <CartPage />
  </Provider>,
  document.querySelector("#root")
);

store.dispatch(actions.initialize({}));

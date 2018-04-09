/*
The store ({@link https://redux.js.org/basics/store}) represents the facts
about "what happened" and the reducers that update the state according
to actions.
 */
import { createStore, applyMiddleware, combineReducers, compose } from "redux";
import createSagaMiddleware from "redux-saga";

// Reducers
import { fieldReducer } from "./field/reducer";

export default function configureStore(initialSate) {
  /*
  Allows us to use Redux Devtools
  {@link https://github.com/zalmoxisus/redux-devtools-extension}
   */
  const composeEnhancers =
    typeof window === "object" && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
      ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({})
      : compose;

  /*
  Set up redux-saga's. {@link https://redux-saga.js.org/}
  This will be used for all asynchronous actions (fetching & updating the server)
 */
  const sagaMiddleware = createSagaMiddleware();
  const enhancer = composeEnhancers(applyMiddleware(sagaMiddleware));

  return {
    ...createStore(combineReducers({ fieldReducer }), initialSate, enhancer),
    runSaga: sagaMiddleware.run
  };
}

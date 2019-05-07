/*
The store ({@link https://redux.js.org/basics/store}) represents the facts
about "what happened" and the reducers that update the state according
to actions.
 */
import { applyMiddleware, combineReducers, compose, createStore } from "redux";
import createSagaMiddleware from "redux-saga";
// Default reducers
import * as defaultReducers from "./reducers";
import * as defaultSagas from "./sagas";

/**
 * Set up the redux store.
 * Installs middle wear into redux for:
 *  - Redux Devtools
 *  - Redux Sagas
 * @param {object} reducers
 * @param {object} sagas
 * @param initialState
 * @returns {Store<any> & {dispatch: any}}
 */
export function getStore(reducers = {}, sagas = {}, initialState) {
  // Add default application reducers
  Object.assign(reducers, defaultReducers);
  Object.assign(sagas, defaultSagas);

  /*
  Allows us to use Redux Devtools
  {@link https://github.com/zalmoxisus/redux-devtools-extension}
   */
  const composeEnhancers =
    typeof window === "object" && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
      ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({
          trace: true,
          traceLimit: 25
        })
      : compose;

  /*
  Set up redux-saga's. {@link https://redux-saga.js.org/}
  This will be used for all asynchronous actions (fetching & updating the server)
 */
  const sagaMiddleware = createSagaMiddleware();
  const enhancer = composeEnhancers(applyMiddleware(sagaMiddleware));
  const store = createStore(combineReducers(reducers), initialState, enhancer);
  // Bind the sagas to the saga middleware
  Object.values(sagas).forEach(sagaMiddleware.run.bind(sagaMiddleware));

  return store;
}

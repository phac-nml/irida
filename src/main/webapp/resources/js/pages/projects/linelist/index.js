/**
 * Root file for the linelist page.
 */
import { createStore, applyMiddleware, compose } from "redux";
import createSagaMiddleware from "redux-saga";
import reducers from "./reducers";
import { fieldWatcherSaga } from "./sagas";
import { initializeApp, play } from "./actions";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

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

const store = createStore(reducers, enhancer);

sagaMiddleware.run(fieldWatcherSaga);
store.dispatch(initializeApp(PROJECT_ID));

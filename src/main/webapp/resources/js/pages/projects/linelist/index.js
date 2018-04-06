/**
 * Root file for the linelist page.
 */
import { createStore, applyMiddleware, compose } from "redux";
import createSagaMiddleware from "redux-saga";
import tableReducer from "./containers/MetadataTable/reducer";
import { initializeMetadataData } from "./containers/MetadataTable/actions";

/*
Allows us to use Redux Devtools
{@link https://github.com/zalmoxisus/redux-devtools-extension}
 */
const composeEnhancers =
  typeof window === "object" && window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__
    ? window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__({})
    : compose;

const sagaMiddleware = createSagaMiddleware();
const enhancer = composeEnhancers(applyMiddleware(sagaMiddleware));

const store = createStore(tableReducer, enhancer);

sagaMiddleware.run(initializeMetadataData);

/**
 * These are only here now to test the API.
 */
// MetadataEntryApi.getAllMetadataEntries(window.project.id).then(
//   result => console.log(result),
//   err => console.error(err)
// );
//
// MetadataFieldApi.getAllMetadataFields(window.project.id).then(
//   result => console.log(result),
//   err => console.error(err)
// );

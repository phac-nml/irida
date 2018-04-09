import configureStore from "./configureStore";
import { initializeApp } from "./app/actions";

// Fields
import fieldApi from "./field/FieldApi";
import { fieldWatcherSaga } from "./field/sagas";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

const store = configureStore();
store.runSaga(fieldWatcherSaga, { api: fieldApi, id: PROJECT_ID });

/*
Initialize the application.
 */
store.dispatch(initializeApp());

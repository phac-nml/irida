import createStore from "./redux/create";
import { initializeApp } from "./app/actions";

// Fields
import fieldApi from "./apis/field";
import { fieldWatcherSaga } from "./redux/modules/field";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

const store = createStore();
store.runSaga(fieldWatcherSaga, { api: fieldApi, id: PROJECT_ID });

/*
Initialize the application.
 */
store.dispatch(initializeApp());

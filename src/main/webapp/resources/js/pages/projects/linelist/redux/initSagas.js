import * as sagas from "./modules/sagas";

export const initSagas = sagaMiddleware => {
  Object.values(sagas).forEach(sagaMiddleware.run.bind(sagaMiddleware));
};

import { configureStore } from "@reduxjs/toolkit";
import { usersApi } from "../../apis/users/users";
import { passwordResetApi } from "../../apis/passwordReset";
import { projectSubscriptionsApi } from "../../apis/projects/project-subscriptions";
import { userReducer } from "./services/userReducer";

/*
Redux Store for user details.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    userReducer,
    [passwordResetApi.reducerPath]: passwordResetApi.reducer,
    [projectSubscriptionsApi.reducerPath]: projectSubscriptionsApi.reducer,
    [usersApi.reducerPath]: usersApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      passwordResetApi.middleware,
      projectSubscriptionsApi.middleware,
      usersApi.middleware,
    ),
});

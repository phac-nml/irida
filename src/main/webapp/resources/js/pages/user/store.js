import { configureStore } from "@reduxjs/toolkit";
import { usersApi } from "../../apis/users/users";
import { passwordResetApi } from "../../apis/passwordReset";
import { userReducer } from "./services/userReducer";

/*
Redux Store for user details.
For more information on redux stores see: https://redux.js.org/tutorials/fundamentals/part-4-store
 */
export default configureStore({
  reducer: {
    userReducer,
    [usersApi.reducerPath]: usersApi.reducer,
    [passwordResetApi.reducerPath]: passwordResetApi.reducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(
      usersApi.middleware,
      passwordResetApi.middleware
    ),
});

import { render } from "react-dom";
import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import { Provider } from "react-redux";
import { setBaseUrl } from "../../utilities/url-utilities";

import store from "./store";
import UserAccountLayout from "./components/UserAccountLayout";
import UserDetailsPage from "./components/UserDetailsPage";
import UserGroupsPage from "./components/UserGroupsPage";
import UserProjectsPage from "./components/UserProjectsPage";
import UserPasswordPage from "./components/UserPasswordPage";

render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/users")}>
      <Routes>
        <Route path="/:userId" element={<UserAccountLayout />}>
          <Route index element={<UserDetailsPage />} />
          <Route path="details" element={<UserDetailsPage />} />
          <Route path="groups" element={<UserGroupsPage />} />
          <Route path="projects" element={<UserProjectsPage />} />
          <Route path="password" element={<UserPasswordPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  </Provider>,
  document.querySelector("#user-account-root")
);

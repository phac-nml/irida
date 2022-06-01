import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { ContentLoading } from "../../components/loader";
import { setBaseUrl } from "../../utilities/url-utilities";
import UserDetailsPage from "./components/UserDetailsPage";
import store from "./store";
import UserAccountApp from "./components/UserAccountApp";

const UserProjectsPage = React.lazy(() =>
  import("./components/UserProjectsPage")
);
const UserSecurityPage = React.lazy(() =>
  import("./components/UserSecurityPage")
);

// Manager: NO ROUTES on view page
// ADMIN: NO ROUTES - router embedded
// USER ACCOUNT: NEEDS ROUTES

/**
 * React component that displays the user pages.
 * @returns {*}
 * @constructor
 */
render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/users")}>
      <Routes>
        <Route path="/:userId" element={<UserAccountApp />}>
          <Route index element={<UserDetailsPage />} />
          <Route path="*" element={<UserDetailsPage />} />
          <Route
            path="projects"
            element={
              <React.Suspense fallback={<ContentLoading />}>
                <UserProjectsPage />
              </React.Suspense>
            }
          />
          <Route
            path="security"
            element={
              <React.Suspense fallback={<ContentLoading />}>
                <UserSecurityPage />
              </React.Suspense>
            }
          />
        </Route>
      </Routes>
    </BrowserRouter>
  </Provider>,
  document.querySelector("#user-account-root")
);

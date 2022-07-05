import { render } from "react-dom";
import React from "react";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Provider } from "react-redux";
import { setBaseUrl } from "../../utilities/url-utilities";
import { ContentLoading } from "../../components/loader";
import store from "./store";

const UserAccountLayout = React.lazy(() =>
  import("./components/UserAccountLayout")
);
const UserDetailsPage = React.lazy(() =>
  import("./components/UserDetailsPage")
);
const UserProjectsPage = React.lazy(() =>
  import("./components/UserProjectsPage")
);
const UserSecurityPage = React.lazy(() =>
  import("./components/UserSecurityPage")
);

/*
WEBPACK PUBLIC PATH:
Webpack does not know what the servlet context path is.  To fix this, webpack exposed
the variable `__webpack_public_path__`
See: https://webpack.js.org/guides/public-path/#on-the-fly
 */
__webpack_public_path__ = setBaseUrl(`/dist/`);

/**
 * React component that displays the user pages.
 * @returns {*}
 * @constructor
 */
render(
  <Provider store={store}>
    <BrowserRouter basename={setBaseUrl("/users")}>
      <React.Suspense fallback={<ContentLoading />}>
        <Routes>
          <Route path="/:userId" element={<UserAccountLayout />}>
            <Route index element={<UserDetailsPage />} />
            <Route path="*" element={<UserDetailsPage />} />
            <Route path="projects" element={<UserProjectsPage />} />
            <Route path="security" element={<UserSecurityPage />} />
          </Route>
        </Routes>
      </React.Suspense>
    </BrowserRouter>
  </Provider>,
  document.querySelector("#user-account-root")
);

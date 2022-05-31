import React from "react";
import { Route } from "react-router-dom";
import { ContentLoading } from "../components/loader/index";
import { UserAccountApp } from "../pages/user/index";

const UserDetailsPage = React.lazy(() =>
  import("../pages/user/components/UserDetailsPage")
);
const UserProjectsPage = React.lazy(() =>
  import("../pages/user/components/UserProjectsPage")
);
const UserSecurityPage = React.lazy(() =>
  import("../pages/user/components/UserSecurityPage")
);

export const userAccountRoutes = (
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
);

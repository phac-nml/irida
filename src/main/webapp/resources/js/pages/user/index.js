import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import { BrowserRouter, Route, Routes, useParams } from "react-router-dom";
import { fetchCurrentUserDetails } from "../../apis/users/user";
import { useGetUserDetailsQuery } from "../../apis/users/users";
import { ContentLoading } from "../../components/loader";
import { NarrowPageWrapper } from "../../components/page/NarrowPageWrapper";
import UserStatusTag from "../../components/user/UserStatusTag";
import { setBaseUrl } from "../../utilities/url-utilities";
import UserDetailsPage from "./components/UserDetailsPage";
import store from "./store";

const UserAccountEditLayout = React.lazy(() =>
  import("./components/UserAccountEditLayout")
);

const UserAccountViewLayout = React.lazy(() =>
  import("./components/UserAccountViewLayout")
);

const UserProjectsPage = React.lazy(() =>
  import("./components/UserProjectsPage")
);
const UserSecurityPage = React.lazy(() =>
  import("./components/UserSecurityPage")
);

function UserAccountApp() {
  const { userId } = useParams();
  const [current, setCurrent] = React.useState();

  const { data, isSuccess } = useGetUserDetailsQuery(userId);

  React.useEffect(() => {
    fetchCurrentUserDetails().then((response) => setCurrent(response));
  }, []);

  return (
    <NarrowPageWrapper
      title={
        isSuccess &&
        `${data.user.firstName} ${data.user.lastName} (${data.user.username})`
      }
      subTitle={
        data?.user.enabled && <UserStatusTag enabled={data.user.enabled} />
      }
    >
      {current === undefined ? (
        <ContentLoading />
      ) : current.admin || Number(current.identifier) === Number(userId) ? (
        <React.Suspense fallback={<ContentLoading />}>
          <UserAccountEditLayout />
        </React.Suspense>
      ) : (
        <React.Suspense fallback={<ContentLoading />}>
          <UserAccountViewLayout />
        </React.Suspense>
      )}
    </NarrowPageWrapper>
  );
}

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

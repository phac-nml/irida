import React from "react";
import { useNavigate, useParams } from "react-router-dom";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { fetchCurrentUserDetails } from "../../../apis/users/user";
import { NarrowPageWrapper } from "../../../components/page/NarrowPageWrapper";
import UserStatusTag from "../../../components/user/UserStatusTag";
import { ContentLoading } from "../../../components/loader";

const UserAccountEditLayout = React.lazy(() =>
  import("./UserAccountEditLayout")
);

const UserAccountViewLayout = React.lazy(() =>
  import("./UserAccountViewLayout")
);

export default function UserAccountApp() {
  const navigate = useNavigate();

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
      onBack={document.referrer.includes("/users") ? () => navigate(-1) : null}
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

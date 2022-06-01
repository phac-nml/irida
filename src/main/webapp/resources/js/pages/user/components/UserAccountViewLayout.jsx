import { Card } from "antd";
import React from "react";
import { useParams } from "react-router-dom";
import { useGetUserDetailsQuery } from "../../../apis/users/users";
import { BasicList } from "../../../components/lists/index";
import SystemRoleTag from "../../../components/roles/SystemRoleTag.jsx";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";

export default function UserAccountViewLayout() {
  const { userId } = useParams();
  const { data, isSuccess } = useGetUserDetailsQuery(userId);
  const [details, setDetails] = React.useState([]);

  const formatUserDetails = (user) => [
    {
      title: i18n("UserDetailsPage.form.firstName.label"),
      desc: user.firstName,
    },
    {
      title: i18n("UserDetailsPage.form.lastName.label"),
      desc: user.lastName,
    },
    {
      title: i18n("UserDetailsPage.form.email.label"),
      desc: <a href={`mailto:${user.email}`}>{user.email}</a>,
    },
    {
      title: i18n("UserDetailsPage.form.phoneNumber.label"),
      desc: user.phoneNumber,
    },
    {
      title: i18n("UserDetailsPage.form.role.label"),
      desc: <SystemRoleTag role={user.role} />,
    },
    {
      title: i18n("UserDetailsPage.createdDate"),
      desc: formatInternationalizedDateTime(user.createdDate),
    },
    {
      title: i18n("UserDetailsPage.modifiedDate"),
      desc: formatInternationalizedDateTime(user.modifiedDate),
    },
  ];

  React.useEffect(() => {
    if (isSuccess) {
      setDetails(formatUserDetails(data.user));
    }
  }, [data?.user, isSuccess]);

  return (
    <Card>
      <BasicList dataSource={details} />
    </Card>
  );
}

import React from "react";
import { PagedTable } from "../../../../components/ant.design/PagedTable";
import { formatDate } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";

export default function AnnouncementUserTable() {
  const columns = [
    {
      title: i18n("announcement.control.details.username"),
      dataIndex: "username",
      width: 400,
      render(text, item) {
        return (
          <a href={setBaseUrl(`/users/${item.user.identifier}`)}>
            {item.user.username}
          </a>
        );
      },
      sorter: true,
    },
    {
      title: i18n("announcement.control.details.dateRead"),
      dataIndex: "dateRead",
      render(date) {
        return <time>{date ? formatDate({ date: date }) : ""}</time>;
      },
    },
  ];

  return <PagedTable columns={columns} />;
}

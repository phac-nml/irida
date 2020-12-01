import React from "react";
import { PagedTable } from "../../../../components/ant.design/PagedTable";
import { formatDate } from "../../../../utilities/date-utilities";
import { IconCheckCircle } from "../../../../components/icons/Icons";
import { blue6, grey2 } from "../../../../styles/colors";

export default function AnnouncementUserTable() {
  const columns = [
    {
      title: i18n("announcement.control.details.username"),
      dataIndex: "name",
    },
    {
      title: i18n("announcement.control.details.status"),
      dataIndex: "status",
      align: "center",
      sorter: true,
      render(text, item) {
        return (
          <IconCheckCircle style={{ color: item.dateRead ? blue6 : grey2 }} />
        );
      },
    },
    {
      title: i18n("announcement.control.details.dateRead"),
      dataIndex: "dateRead",
      render(date) {
        return <time>{formatDate({ date: date })}</time>;
      },
    },
  ];

  return <PagedTable columns={columns} />;
}

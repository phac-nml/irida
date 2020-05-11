import React, { useEffect, useState } from "react";
import { getProjectsForUserGroup } from "../../../apis/users/groups";
import { Button, Table } from "antd";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";

export function UserGroupProjectsTable({ groupId }) {
  const [projects, setProjects] = useState();
  const columns = [
    {
      dataIndex: "name",
      title: i18n("UserGroupProjectTable.name"),
      render(text, item) {
        return (
          <Button href={setBaseUrl(`/projects/${item.id}`)} type="link">
            {text}
          </Button>
        );
      },
    },
    { dataIndex: "role", title: i18n("UserGroupProjectTable.role") },
    {
      title: i18n("UserGroupProjectTable.create"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  useEffect(() => {
    getProjectsForUserGroup(groupId).then((data) => setProjects(data));
  }, [groupId]);

  return <Table columns={columns} dataSource={projects} />;
}

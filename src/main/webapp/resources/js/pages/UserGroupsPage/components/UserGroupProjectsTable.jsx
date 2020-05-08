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
      title: "NAME",
      render(text, item) {
        return (
          <Button href={setBaseUrl(`/projects/${item.id}`)} type="link">
            {text}
          </Button>
        );
      },
    },
    { dataIndex: "role", title: "PROJECT ROLE" },
    {
      title: "DATE ADDED",
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

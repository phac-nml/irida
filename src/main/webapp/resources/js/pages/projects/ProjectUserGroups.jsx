import React from "react";
import { render } from "react-dom";
import { PagedTableProvider } from "../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../utilities/url-utilities";
import { Typography } from "antd";
import { PagedTable } from "../../components/ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";

const { Title } = Typography;

function ProjectUserGroupsTable({}) {
  const columns = [
    {
      dataIndex: "name",
      title: "NAME++",
    },
    {
      dataIndex: "role",
      title: "role",
    },
    {
      dataIndex: "createdDate",
      title: "DATE ADDED",
      render(date) {
        return formatInternationalizedDateTime(date);
      },
    },
  ];

  return <PagedTable columns={columns} />;
}

function ProjectUserGroups() {
  return (
    <PagedTableProvider
      url={setBaseUrl(`/ajax/projects/${window.project.id}/groups`)}
    >
      <>
        <Title level={2}>USER GROUPS__</Title>
        <ProjectUserGroupsTable />
      </>
    </PagedTableProvider>
  );
}

render(<ProjectUserGroups />, document.querySelector("#groups-root"));

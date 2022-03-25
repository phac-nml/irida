import React from "react";
import { render } from "react-dom";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import { Table } from "antd";

function SamplesTable() {
  const projectId = getProjectIdFromUrl();

  React.useEffect(() => {
    const params = new URLSearchParams();
    params.append("projectIds", projectId);
    params.append("projectIds", 5);
    // Add associated projectIds here.

    fetch(`/ajax/project-samples?${params.toString()}`, {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        pageSize: 10,
        pageNumber: 0,
      }),
    });
  }, []);

  const columns = [
    {
      title: "Name",
      dataIndex: "name",
      key: "name",
    },
    {
      title: "Organism",
      dataIndex: "organism",
      key: "organism",
    },
    {
      title: "Project",
      dataIndex: "project",
      key: "project",
    },
    {
      title: "Created",
      dataIndex: "created",
      key: "created",
    },
    {
      modified: "Modified",
      dataIndex: "modified",
      key: "modified",
    },
  ];

  return <Table columns={columns} />;
}

render(
  <div>
    <SamplesTable />
  </div>,
  document.getElementById("root")
);

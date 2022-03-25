import React from "react";
import { render } from "react-dom";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import { Table } from "antd";
import axios from "axios";

function SamplesTable() {
  const [samples, setSamples] = React.useState([]);
  const [pagination, setPagination] = React.useState({
    current: 1,
    pageSize: 10,
  });
  const [selectedRowKeys, setSelectedRowKeys] = React.useState([]);
  const projectId = getProjectIdFromUrl();

  React.useEffect(() => {
    const params = new URLSearchParams();
    params.append("projectIds", projectId);
    params.append("projectIds", 5);
    // Add associated projectIds here.

    axios
      .post(`/ajax/project-samples?${params.toString()}`, pagination)
      .then(({ data }) => {
        setSamples(data.content);
        setPagination({ ...pagination, total: data.total });
      });
  }, []);

  const rowSelection = {
    selectedRowKeys,
    onChange: (selectedRowKeys) => {
      setSelectedRowKeys(selectedRowKeys);
    },
    onSelectAll: (selected, selectedRows, changeRows) => {
      console.log({ selected, selectedRows, changeRows });
      // TODO: Call to server to get all samples for selected projects.
    },
  };

  const handleTableChange = (pagination, filters, sorter) => {
    setPagination({ ...pagination });
    // TODO: handle filter
    // TODO: handle sort

    const params = new URLSearchParams();
    params.append("projectIds", projectId);
    params.append("projectIds", 5);
    // Add associated projectIds here.
    axios
      .post(`/ajax/project-samples?${params.toString()}`, pagination)
      .then(({ data }) => {
        setSamples(data.content);
        setPagination({ ...pagination, total: data.total });
      });
  };

  const columns = [
    {
      title: "Name",
      dataIndex: ["sample", "sampleName"],
      key: "name",
      render: (name, row, index) => <a>{name}</a>,
    },
    {
      title: "Organism",
      dataIndex: ["sample", "organism"],
      key: "organism",
    },
    {
      title: "Project",
      dataIndex: ["project", "name"],
      key: "project",
    },
    {
      title: "Created",
      dataIndex: ["sample", "createdDate"],
      key: "created",
    },
    {
      title: "Modified",
      dataIndex: ["sample", "modifiedDate"],
      key: "modified",
    },
  ];

  return (
    <Table
      columns={columns}
      dataSource={samples}
      rowSelection={rowSelection}
      pagination={pagination}
      onChange={handleTableChange}
    />
  );
}

render(
  <div>
    <SamplesTable />
  </div>,
  document.getElementById("root")
);

import React from "react";
import { render } from "react-dom";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import { Table, Tag } from "antd";
import axios from "axios";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";

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
      .post(`/ajax/project-samples?${params.toString()}`, {
        ...pagination,
        order: [{ property: "sample.modifiedDate", direction: "desc" }],
      })
      .then(({ data }) => {
        setSamples(data.content);
        setPagination({ ...pagination, total: data.total });
      });
  }, []);

  const randomColor = Math.floor(Math.random() * 16777215).toString(16);

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
    const order = formatSort(sorter);

    const params = new URLSearchParams();
    params.append("projectIds", projectId);
    params.append("projectIds", 5);
    // Add associated projectIds here.
    axios
      .post(`/ajax/project-samples?${params.toString()}`, {
        ...pagination,
        order,
      })
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
      sorter: { multiple: 3 },
      render: (name, row, index) => <a>{name}</a>,
    },
    {
      title: "Organism",
      dataIndex: ["sample", "organism"],
      key: "organism",
      sorter: { multiple: true },
    },
    {
      title: "Project",
      dataIndex: ["project", "name"],
      key: "project",
      render: (name, row, index) => <Tag color={`#${randomColor}`}>{name}</Tag>,
    },
    {
      title: "Created",
      dataIndex: ["sample", "createdDate"],
      key: "created",
      sorter: { multiple: 2 },
      render: (createdDate, row, index) => {
        return formatInternationalizedDateTime(createdDate);
      },
    },
    {
      title: "Modified",
      dataIndex: ["sample", "modifiedDate"],
      key: "modified",
      defaultSortOrder: "descend",
      sorter: { multiple: 1 },
      render: (modifiedDate, row, index) => {
        return formatInternationalizedDateTime(modifiedDate);
      },
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

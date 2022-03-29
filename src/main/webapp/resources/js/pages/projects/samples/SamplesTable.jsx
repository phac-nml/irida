import React from "react";
import {
  getProjectIdFromUrl,
  setBaseUrl,
} from "../../../utilities/url-utilities";
import { Table, Tag, Tooltip } from "antd";
import axios from "axios";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";
import { getNewTagColor } from "../../../utilities/ant-utilities";
import {
  getAllSampleIds,
  getAssociatedProjectForProject,
  getPagedProjectSamples,
} from "../../../apis/projects/project-samples";
import { FolderAddOutlined } from "@ant-design/icons";
import { blue6 } from "../../../styles/colors";

export function SamplesTable() {
  const [loading, setLoading] = React.useState(true);
  const [samples, setSamples] = React.useState([]);

  const [pagination, setPagination] = React.useState({
    current: 1,
    pageSize: 10,
  });
  const [associated, setAssociated] = React.useState([]);
  const [selectedRowKeys, setSelectedRowKeys] = React.useState([]);
  const [colors, setColors] = React.useState(() => {
    const colorString = localStorage.getItem("projectColors");
    return colorString ? JSON.parse(colorString) : {};
  });
  const projectId = getProjectIdFromUrl();

  React.useEffect(() => {
    const samplesPromise = getPagedProjectSamples(projectId, {
      ...pagination,
      order: [{ property: "sample.modifiedDate", direction: "desc" }],
    });

    const associatedPromise = getAssociatedProjectForProject(projectId);

    axios.all([samplesPromise, associatedPromise]).then(
      axios.spread((samplesResponse, associatedResponse) => {
        setSamples(samplesResponse.data.content);
        setPagination({ ...pagination, total: samplesResponse.data.total });
        setAssociated(
          associatedResponse.data.map((item) => ({
            text: item.label,
            value: item.id,
          }))
        );

        // Get colors for all associated projects
        const newColors = { ...colors };
        associatedResponse.data.forEach((project) => {
          if (!newColors[project.id]) {
            newColors[project.id] = getNewTagColor();
          }
        });
        setColors(newColors);
        localStorage.setItem("projectColors", JSON.stringify(newColors));
        setLoading(false);
      })
    );
  }, []);

  const rowSelection = {
    selectedRowKeys,
    preserveSelectedRowKeys: true,
    onChange: (selectedRowKeys) => {
      setSelectedRowKeys(selectedRowKeys);
    },
    onSelectAll: (selected, selectedRows, changeRows) => {
      console.log(selectedRowKeys);
      setLoading(true);
      // Get all the sample identifier
      if (selected) {
        getAllSampleIds(
          projectId,
          associated.map((project) => project.value)
        ).then(({ data }) => {
          console.log(data);
        });
      } else {
        setSelectedRowKeys([]);
      }
      setLoading(false);
    },
  };

  const handleTableChange = async (pagination, filters, sorter) => {
    setLoading(true);
    // TODO: handle filter
    let associated;
    if (filters.project?.length) {
      associated = filters.project;
    }

    // Handle Sort
    const order = formatSort(sorter);
    // Add associated projectIds here.

    const { data } = await getPagedProjectSamples(projectId, {
      ...pagination,
      order,
      associated,
    });
    setSamples(data.content);
    setPagination({ ...pagination, total: data.total });
    setLoading(false);
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
      sorter: { multiple: true },
      key: "project",
      render: (name, row, index) => {
        return <Tag color={colors[row.project.id]}>{name}</Tag>;
      },
      filters: associated,
      filterIcon: () => (
        <Tooltip title={"Associated Projects"}>
          <FolderAddOutlined style={{ color: blue6 }} />
        </Tooltip>
      ),
    },
    {
      title: "Created",
      dataIndex: ["sample", "createdDate"],
      key: "created",
      sorter: { multiple: 2 },
      width: 230,
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
      width: 230,
      render: (modifiedDate, row, index) => {
        return formatInternationalizedDateTime(modifiedDate);
      },
    },
  ];

  return (
    <Table
      loading={loading}
      columns={columns}
      dataSource={samples}
      rowSelection={rowSelection}
      pagination={pagination}
      onChange={handleTableChange}
      summary={() => (
        <Table.Summary.Row>
          <Table.Summary.Cell colSpan={5}>
            Selected: {selectedRowKeys.length} of {pagination.total}
          </Table.Summary.Cell>
        </Table.Summary.Row>
      )}
    />
  );
}

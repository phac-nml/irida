import React from "react";
import {
  DownOutlined,
  FolderAddOutlined,
  MergeCellsOutlined,
} from "@ant-design/icons";
import {
  Button,
  Checkbox,
  Col,
  Dropdown,
  Menu,
  Row,
  Space,
  Table,
  Tag,
  Tooltip,
} from "antd";
import axios from "axios";
import { getAssociatedProjectForProject } from "../../../apis/projects/associated-projects";
import {
  getAllSampleIds,
  getPagedProjectSamples,
} from "../../../apis/projects/project-samples";
import { blue6 } from "../../../styles/colors";
import { getNewTagColor } from "../../../utilities/ant-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import MergeSamples from "./components/MergeSamples";
import SampleIcons from "./components/SampleIcons";

const formatCartItem = (item) => ({
  key: item.key,
  id: item.sample.id,
  projectId: item.project.id,
  sampleName: item.sample.sampleName,
  owner: item.owner,
});

export function SamplesTable() {
  const [loading, setLoading] = React.useState(true);
  const [samples, setSamples] = React.useState([]);
  const [selectedItems, setSelectedItems] = React.useState({});
  const [pagination, setPagination] = React.useState({
    current: 1,
    pageSize: 10,
  });
  const [filters, setFilters] = React.useState({ associated: null });
  const [order, setOrder] = React.useState();
  const [associated, setAssociated] = React.useState([]);
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

  const selectRow = (event, item) => {
    const selected = event.target.checked;
    if (selected) {
      setSelectedItems({ ...selectedItems, [item.key]: formatCartItem(item) });
    } else {
      const updatedSelected = { ...selectedItems };
      delete updatedSelected[item.key];
      setSelectedItems(updatedSelected);
    }
  };

  const selectAll = async () => {
    setLoading(true);
    const { data } = await getAllSampleIds(projectId, filters);
    const newSelected = {};
    data.forEach((item) => (newSelected[item.key] = item));
    setSelectedItems(newSelected);
    setLoading(false);
  };

  const selectNone = () => setSelectedItems([]);

  const updateSelectAll = (e) =>
    e.target.checked ? selectAll() : selectNone();

  const handleTableChange = async (pagination, newFilters, sorter) => {
    setLoading(true);
    // Save the filters for using when selecting all
    if (
      Object.entries(filters).toString() !==
      Object.entries(newFilters).toString()
    ) {
      setFilters(newFilters);

      // Clear selections since filters changed
      setSelectedItems([]);
    }

    // Handle Sort
    const order = formatSort(sorter);
    setOrder(order);
    // Add associated projectIds here.

    const { data } = await getPagedProjectSamples(projectId, {
      ...pagination,
      order,
      filters: newFilters,
    });
    setSamples(data.content);
    setPagination({ ...pagination, total: data.total });
    setLoading(false);
  };

  const reloadTable = async () => {
    setLoading(true);
    const { data } = await getPagedProjectSamples(projectId, {
      ...pagination,
      order,
      filters,
    });
    setSamples(data.content);

    // Clear selections since filters changed
    setSelectedItems([]);
    setLoading(false);
  };

  const columns = [
    {
      title: () => {
        const length = Object.keys(selectedItems).length;
        const indeterminate = length < pagination.total && length > 0;
        return (
          <Checkbox
            onChange={updateSelectAll}
            checked={length > 0}
            indeterminate={indeterminate}
          />
        );
      },
      dataIndex: "key",
      width: 40,
      render: (text, item) => {
        return (
          <Space>
            <Checkbox
              onChange={(e) => selectRow(e, item)}
              checked={selectedItems[item.key]}
            />
            <SampleIcons sample={item} />
          </Space>
        );
      },
    },
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
      key: "associated",
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
      render: (createdDate) => {
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
      render: (modifiedDate) => {
        return formatInternationalizedDateTime(modifiedDate);
      },
    },
  ];

  return (
    <Row gutter={[16, 16]}>
      <Col span={24}>
        <Space wrap>
          <Dropdown
            overlay={
              <Menu>
                <MergeSamples samples={selectedItems} updateTable={reloadTable}>
                  <Menu.Item
                    icon={<MergeCellsOutlined />}
                    disabled={filters.associated}
                  >
                    Merges Samples
                  </Menu.Item>
                </MergeSamples>
              </Menu>
            }
          >
            <Button>
              Sample Tools <DownOutlined />
            </Button>
          </Dropdown>
        </Space>
      </Col>
      <Col span={24}>
        <Table
          loading={loading}
          columns={columns}
          dataSource={samples}
          pagination={pagination}
          onChange={handleTableChange}
          summary={() => (
            <Table.Summary.Row>
              <Table.Summary.Cell colSpan={5}>
                {`Selected: ${Object.keys(selectedItems).length} of
                ${pagination.total}`}
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )}
        />
      </Col>
    </Row>
  );
}

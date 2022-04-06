import React from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  DownOutlined,
  FolderAddOutlined,
  MergeCellsOutlined
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
  Tooltip
} from "antd";
import { useListAssociatedProjectsQuery } from "../../../apis/projects/associated-projects";
import {
  getAllSampleIds,
  getPagedProjectSamples
} from "../../../apis/projects/project-samples";
import { blue6 } from "../../../styles/colors";
import { getNewTagColor } from "../../../utilities/ant-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";
import { getProjectIdFromUrl } from "../../../utilities/url-utilities";
import MergeSamples from "./components/MergeSamples";
import SampleIcons from "./components/SampleIcons";
import { useListSamplesQuery } from "./services/samples";
import { updateTable } from "./sample.store";

const formatCartItem = item => ({
  key: item.key,
  id: item.sample.id,
  projectId: item.project.id,
  sampleName: item.sample.sampleName,
  owner: item.owner
});

export function SamplesTable() {
  const dispatch = useDispatch();
  const { projectId, options } = useSelector(state => state.samples);
  const {
    data: { content: samples, total } = {},
    isFetching
  } = useListSamplesQuery(
    {
      ...options.pagination, // maybe we should not have to spread this?
      order: options.order,
      filters: options.filters
    },
    {
      refetchOnMountOrArgChange: true
    }
  );

  const { data: associated } = useListAssociatedProjectsQuery(projectId);

  const [selectedItems, setSelectedItems] = React.useState({});

  const [colors, setColors] = React.useState(() => {
    const colorString = localStorage.getItem("projectColors");
    return colorString ? JSON.parse(colorString) : {};
  });

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
    const { data } = await getAllSampleIds(projectId, filters);
    const newSelected = {};
    data.forEach(item => (newSelected[item.key] = item));
    setSelectedItems(newSelected);
  };

  const selectNone = () => setSelectedItems([]);

  const updateSelectAll = e => (e.target.checked ? selectAll() : selectNone());

  const handleTableChange = async (pagination, filters, sorter) => {
    dispatch(
      updateTable({
        filters,
        pagination,
        order: formatSort(sorter)
      })
    );
    // setLoading(true);
    // // Save the filters for using when selecting all
    // if (
    //   Object.entries(filters).toString() !==
    //   Object.entries(newFilters).toString()
    // ) {
    //   setFilters(newFilters);
    //
    //   // Clear selections since filters changed
    //   setSelectedItems([]);
    // }
    //
    // // Handle Sort
    // const order = formatSort(sorter);
    // setOrder(order);
    // // Add associated projectIds here.
    //
    // const { data } = await getPagedProjectSamples(projectId, {
    //   ...pagination,
    //   order,
    //   filters: newFilters
    // });
    // setSamples(data.content);
    // setPagination({ ...pagination, total: data.total });
    // setLoading(false);
  };

  const reloadTable = async () => {
    // setLoading(true);
    // const { data } = await getPagedProjectSamples(projectId, {
    //   ...pagination,
    //   order,
    //   filters
    // });
    // setSamples(data.content);
    // setPagination({ ...pagination, total: data.total });
    //
    // // Clear selections since filters changed
    // setSelectedItems([]);
    // setLoading(false);
  };

  const columns = [
    {
      title: () => {
        const length = Object.keys(selectedItems).length;
        const indeterminate = length < total && length > 0;
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
              onChange={e => selectRow(e, item)}
              checked={selectedItems[item.key]}
            />
            <SampleIcons sample={item} />
          </Space>
        );
      }
    },
    {
      title: "Name",
      dataIndex: ["sample", "sampleName"],
      key: "name",
      sorter: { multiple: 3 },
      render: name => <a>{name}</a>
    },
    {
      title: "Organism",
      dataIndex: ["sample", "organism"],
      key: "organism",
      sorter: { multiple: true }
    },
    {
      title: "Project",
      dataIndex: ["project", "name"],
      sorter: { multiple: true },
      key: "associated",
      render: (name, row) => {
        return <Tag color={colors[row.project.id]}>{name}</Tag>;
      },
      filters: associated,
      filterIcon: () => (
        <Tooltip title={"Associated Projects"}>
          <FolderAddOutlined style={{ color: blue6 }} />
        </Tooltip>
      )
    },
    {
      title: "Created",
      dataIndex: ["sample", "createdDate"],
      key: "created",
      sorter: { multiple: 2 },
      width: 230,
      render: createdDate => {
        return formatInternationalizedDateTime(createdDate);
      }
    },
    {
      title: "Modified",
      dataIndex: ["sample", "modifiedDate"],
      key: "modified",
      defaultSortOrder: "descend",
      sorter: { multiple: 1 },
      width: 230,
      render: modifiedDate => {
        return formatInternationalizedDateTime(modifiedDate);
      }
    }
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
                    disabled={options.filters.associated}
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
          loading={isFetching}
          columns={columns}
          dataSource={samples}
          pagination={{ ...options.pagination, total }}
          onChange={handleTableChange}
          summary={() => (
            <Table.Summary.Row>
              <Table.Summary.Cell colSpan={5}>
                {`Selected: ${Object.keys(selectedItems).length} of
                ${total}`}
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )}
        />
      </Col>
    </Row>
  );
}

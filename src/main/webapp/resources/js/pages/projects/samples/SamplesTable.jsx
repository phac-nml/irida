import React from "react";
import { useDispatch, useSelector } from "react-redux";
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
import { useListAssociatedProjectsQuery } from "../../../apis/projects/associated-projects";
import { getAllSampleIds } from "../../../apis/projects/project-samples";
import { blue6 } from "../../../styles/colors";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";
import MergeSamples from "./components/MergeSamples";
import SampleIcons from "./components/SampleIcons";
import { useListSamplesQuery } from "./services/samples";
import { updateTable } from "./sample.store";
import { INITIAL_TABLE_STATE } from "./constants";

const formatCartItem = (item) => ({
  key: item.key,
  id: item.sample.id,
  projectId: item.project.id,
  sampleName: item.sample.sampleName,
  owner: item.owner,
});

export function SamplesTable() {
  const dispatch = useDispatch();
  const { projectId, options } = useSelector((state) => state.samples);
  const { data: { content: samples, total } = {}, isFetching } =
    useListSamplesQuery(options, {
      refetchOnMountOrArgChange: true,
    });

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
    data.forEach((item) => (newSelected[item.key] = item));
    setSelectedItems(newSelected);
  };

  const selectNone = () => setSelectedItems([]);

  const updateSelectAll = (e) =>
    e.target.checked ? selectAll() : selectNone();

  /**
   * Handle changes made to the table options.  This will trigger an automatic
   * reload of the table content.
   * NOTE: This is called by the Ant Design table itself, not manually
   * @param pagination
   * @param filters
   * @param sorter
   * @returns {*}
   */
  const _onTableChange = (pagination, filters, sorter) =>
    dispatch(
      updateTable({
        filters,
        pagination,
        order: formatSort(sorter),
      })
    );

  /**
   * Reload the table after a change to the samples, but maintain key options
   * such as the pageSize that the user would have set themselves. This will
   * trigger an automatic reload of the table content.
   * @returns {Promise<void>}
   */
  const reloadTable = () => {
    const tableOptions = { ...INITIAL_TABLE_STATE };

    // Update with options that we want to keep
    tableOptions.pagination.pageSize = options.pagination.pageSize;
    dispatch(updateTable(tableOptions));
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
      render: (name) => <a>{name}</a>,
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
      render: (name, row) => {
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
          onChange={_onTableChange}
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

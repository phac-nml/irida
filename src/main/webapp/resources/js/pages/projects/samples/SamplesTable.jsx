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
import { blue6 } from "../../../styles/colors";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { formatSort } from "../../../utilities/table-utilities";
import MergeSamples from "./components/MergeSamples";
import SampleIcons from "./components/SampleIcons";
import { useListSamplesQuery } from "./services/samples";
import {
  addSelectedSample,
  clearSelectedSamples,
  removeSelectedSample,
  selectAllSamples,
  updateTable,
} from "./services/samplesSlice";
import { INITIAL_TABLE_STATE } from "./constants";

export function SamplesTable() {
  const dispatch = useDispatch();
  const { projectId, options, selected } = useSelector(
    (state) => state.samples
  );

  const { data: { content: samples, total } = {}, isFetching } =
    useListSamplesQuery(options, {
      refetchOnMountOrArgChange: true,
    });

  const { data: associated } = useListAssociatedProjectsQuery(projectId);

  const [colors, setColors] = React.useState(() => {
    const colorString = localStorage.getItem("projectColors");
    return colorString ? JSON.parse(colorString) : {};
  });

  /**
   * Handle row selection change event
   * @param event
   * @param sample
   */
  const onRowSelectionChange = (event, sample) => {
    const selected = event.target.checked;
    if (selected) {
      dispatch(addSelectedSample(sample));
    } else {
      dispatch(removeSelectedSample(sample.key));
    }
  };

  /**
   * Called by select all/none table header
   * @param e - React synthetic event
   * @returns {*}
   */
  const updateSelectAll = (e) =>
    e.target.checked
      ? dispatch(selectAllSamples(projectId, options))
      : dispatch(clearSelectedSamples());

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
        const length = Object.keys(selected).length;
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
              onChange={(e) => onRowSelectionChange(e, item)}
              checked={selected[item.key]}
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
                {`Selected: ${Object.keys(selected).length} of
                ${total}`}
              </Table.Summary.Cell>
            </Table.Summary.Row>
          )}
        />
      </Col>
    </Row>
  );
}

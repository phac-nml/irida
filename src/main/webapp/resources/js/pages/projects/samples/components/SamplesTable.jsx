import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { FolderAddOutlined, SearchOutlined } from "@ant-design/icons";
import { Button, Checkbox, DatePicker, Input, Space, Table, Tag, Tooltip } from "antd";
import { useListAssociatedProjectsQuery } from "../../../../apis/projects/associated-projects";
import { blue6 } from "../../../../styles/colors";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { formatSearch, formatSort } from "../../../../utilities/table-utilities";
import SampleIcons from "./SampleIcons";
import { useListSamplesQuery } from "../services/samples";
import {
  addSelectedSample,
  clearSelectedSamples,
  removeSelectedSample,
  selectAllSamples,
  updateTable
} from "../services/samplesSlice";
import { getNewTagColor } from "../../../../utilities/ant-utilities";

const { RangePicker } = DatePicker;

/**
 * React element to render a table display samples belong to a project,
 * and the project's associated projects.
 * @returns {JSX.Element}
 * @constructor
 */
export function SamplesTable() {
  const dispatch = useDispatch();
  const {
    projectId,
    options,
    selected,
    selectedCount,
    loadingLong
  } = useSelector(state => state.samples);

  /**
   * Fetch the current state of the table.  Will refetch whenever one of the
   * table options (filter, sort, or pagination) changes.
   */
  const {
    data: { content: samples, total } = {},
    isFetching
  } = useListSamplesQuery(options, {
    refetchOnMountOrArgChange: true
  });

  /**
   * Fetch projects that have been associated with this project.
   * Request formats them into a format that can be consumed by the
   * project column filter.
   */
  const { data: associated } = useListAssociatedProjectsQuery(projectId);

  /**
   * Create colors for associated projects. This is stored in local storage for consistency
   * @type {{}}
   */
  const colors = React.useMemo(() => {
    let newColors = {};
    if (associated) {
      const colorString = localStorage.getItem("projectColors");
      newColors = colorString ? JSON.parse(colorString) : {};
      associated.forEach(({ value }) => {
        if (!newColors.hasOwnProperty(value)) {
          newColors[value] = getNewTagColor();
        }
      });
      localStorage.setItem("projectColors", JSON.stringify(newColors));
    }
    return newColors;
  }, [associated]);

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
  const updateSelectAll = e => {
    e.target.checked
      ? dispatch(selectAllSamples({projectId, options}))
      : dispatch(clearSelectedSamples());
  };

  /**
   * Handle changes made to the table options.  This will trigger an automatic
   * reload of the table content.
   * @param pagination
   * @param filters
   * @param sorter
   * @returns {*}
   */
  const onTableChange = (pagination, filters, sorter) =>
    dispatch(
      updateTable({
        filters: {"associated": filters["associated"]},
        pagination,
        order: formatSort(sorter),
        search: formatSearch(filters),
      })
    );

  const handleSearch = (selectedKeys, confirm, dataIndex) => {
    confirm();
  };

  const handleClearSearch = (clearFilters, confirm, dataIndex) => {
    clearFilters();
    confirm({closeDropdown: false});
  }

  const getColumnSearchProps = dataIndex => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
      <div style={{ padding: 8 }}>
        <Input
          placeholder={`Search ${dataIndex}`}
          value={selectedKeys[0]}
          onChange={e => setSelectedKeys(e.target.value ? [e.target.value] : [])}
          onPressEnter={() => handleSearch(selectedKeys, confirm, dataIndex)}
          style={{ marginBottom: 8, display: 'block' }}
        />
        <Space>
          <Button disabled={selectedKeys.length === 0} onClick={() => handleClearSearch(clearFilters, confirm, dataIndex)} size="small" style={{ width: 89 }}>
            Clear
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            Search
          </Button>
        </Space>
      </div>
    ),
    filterIcon: filtered => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
  })

  const getDateColumnSearchProps = dataIndex => ({
    filterDropdown: ({ setSelectedKeys, selectedKeys, confirm, clearFilters }) => (
      <div style={{ padding: 8}}>
        <div style={{ marginBottom: 8, display: 'block'}}>
          <RangePicker
            onChange={(dates, dateStrings) => setSelectedKeys([dates[0].startOf('day'), dates[1].endOf('day')]) }
          />
        </div>
        <Space>
          <Button disabled={selectedKeys.length === 0} onClick={() => handleClearSearch(clearFilters, confirm, dataIndex)} size="small" style={{ width: 89 }}>
            Clear
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm, dataIndex)}
            icon={<SearchOutlined />}
            size="small"
            style={{ width: 90 }}
          >
            Search
          </Button>
        </Space>
      </div>
    ),
    filterIcon: filtered => <SearchOutlined style={{ color: filtered ? '#1890ff' : undefined }} />,
  });

  const columns = [
    {
      title: () => {
        const indeterminate = selectedCount < total && selectedCount > 0;
        return (
          <Checkbox
            onChange={updateSelectAll}
            checked={selectedCount > 0}
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
              onChange={e => onRowSelectionChange(e, item)}
              checked={selected[item.key]}
            />
            <SampleIcons sample={item} />
          </Space>
        );
      }
    },
    {
      title: i18n("SamplesTable.Column.sampleName"),
      dataIndex: ["sample", "sampleName"],
      sorter: { multiple: 1 },
      render: name => <a>{name}</a>,
      ...getColumnSearchProps(["sample", "sampleName"]),
    },
    {
      title: i18n("SamplesTable.Column.organism"),
      dataIndex: ["sample", "organism"],
      sorter: { multiple: 1 },
      ...getColumnSearchProps(["sample", "organism"]),
    },
    {
      title: i18n("SamplesTable.Column.project"),
      dataIndex: ["project", "name"],
      sorter: { multiple: 1 },
      key: "associated",
      render: (name, row) => {
        return <Tag color={colors[row.project.id]}>{name}</Tag>;
      },
      filters: associated,
      filterIcon: () => (
        <Tooltip title={i18n("SamplesTable.Filter.associated")}>
          <FolderAddOutlined style={{ color: blue6 }} />
        </Tooltip>
      )
    },
    {
      title: i18n("SamplesTable.Column.collectedBy"),
      dataIndex: ["sample", "collectedBy"],
      sorter: { multiple: 1 }
    },
    {
      title: i18n("SamplesTable.Column.created"),
      dataIndex: ["sample", "createdDate"],
      sorter: { multiple: 1 },
      width: 230,
      render: createdDate => {
        return formatInternationalizedDateTime(createdDate);
      },
      ...getDateColumnSearchProps(["sample", "createdDate"])
    },
    {
      title: i18n("SamplesTable.Column.modified"),
      dataIndex: ["sample", "modifiedDate"],
      defaultSortOrder: "descend",
      sorter: { multiple: 1 },
      width: 230,
      render: modifiedDate => {
        return formatInternationalizedDateTime(modifiedDate);
      },
      ...getDateColumnSearchProps(["sample", "createdDate"])
    }
  ];

  return (
    <Table
      loading={isFetching || loadingLong}
      columns={columns}
      dataSource={samples}
      pagination={{ ...options.pagination, total }}
      onChange={onTableChange}
      summary={() => (
        <Table.Summary.Row>
          <Table.Summary.Cell colSpan={5}>
            {i18n("SamplesTable.Summary", selectedCount, total)}
          </Table.Summary.Cell>
        </Table.Summary.Row>
      )}
    />
  );
}

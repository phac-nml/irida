import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Checkbox, DatePicker, Select, Space, Table, Tag } from "antd";
import { useListAssociatedProjectsQuery } from "../../../../apis/projects/associated-projects";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import {
  formatSearch,
  formatSort,
} from "../../../../utilities/table-utilities";
import SampleIcons from "./SampleIcons";
import { useListSamplesQuery } from "../../../../apis/projects/samples";
import {
  addSelectedSample,
  clearSelectedSamples,
  removeSelectedSample,
  selectAllSamples,
  updateTable,
} from "../../redux/samplesSlice";
import SampleQuality from "../../../../components/sample-quality";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconSearch } from "../../../../components/icons/Icons";
import { blue6 } from "../../../../styles/colors";
import { generateColourForItem } from "../../../../utilities/colour-utilities";
import { getPaginationOptions } from "../../../../utilities/antdesign-table-utilities";
import { SampleDetailViewer } from "../../../../components/samples/SampleDetailViewer";
import ProjectTag from "../../../search/ProjectTag";

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
    loadingLong,
    filterByFile,
  } = useSelector((state) => state.samples);

  /**
   * Fetch the current state of the table.
   * Re-fetch whenever one of the
   * table options (filter, sort, or pagination) changes.
   */
  const { data: { content: samples, total } = {}, isFetching } =
    useListSamplesQuery(options, {
      refetchOnMountOrArgChange: true,
    });

  /**
   * Fetch projects that have been associated with this project.
   * Request formats them into a format that can be consumed by the
   * project column filter.
   */
  const { data: associatedProjects } =
    useListAssociatedProjectsQuery(projectId);

  /**
   * Handle row selection change event
   * @param event
   * @param sample
   */
  const onRowSelectionChange = (event, sample) => {
    if (event.target.checked) {
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
      ? dispatch(selectAllSamples())
      : dispatch(clearSelectedSamples());

  /**
   * Handle changes made to the table options.  This will trigger an automatic
   * reload of the table content.
   * @param pagination
   * @param tableFilters
   * @param sorter
   * @returns {*}
   */
  const onTableChange = (pagination, tableFilters, sorter) => {
    let { associated, ...filters } = tableFilters;
    const search = formatSearch(filters);
    if (filterByFile) search.push(filterByFile.fileFilter);

    dispatch(
      updateTable({
        filters: { associated: associated === undefined ? null : associated }, // Null conversion for comparison with default values in slice
        pagination,
        order: formatSort(sorter),
        search,
      })
    );
  };

  const handleSearch = (selectedKeys, confirm) => {
    confirm();
  };

  const handleClearSearch = (clearFilters, confirm) => {
    clearFilters();
    confirm({ closeDropdown: false });
  };

  const getColumnSearchProps = (
    dataIndex,
    filterName = "",
    placeholder = ""
  ) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => (
      <div style={{ padding: 8 }}>
        <Select
          className={filterName}
          mode="tags"
          placeholder={placeholder}
          value={selectedKeys[0]}
          onChange={(e) => {
            const values = Array.isArray(e) && e.length > 0 ? [e] : e;
            setSelectedKeys(values);
            confirm({ closeDropdown: false });
          }}
          style={{ marginBottom: 8, display: "block" }}
        />
        <Space>
          <Button
            disabled={selectedKeys.length === 0 || selectedKeys[0].length === 0}
            onClick={() => handleClearSearch(clearFilters, confirm)}
            size="small"
            style={{ width: 89 }}
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm)}
            icon={<IconSearch />}
            size="small"
            style={{ width: 90 }}
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <IconSearch style={{ color: filtered ? blue6 : undefined }} />
    ),
  });

  const getDateColumnSearchProps = (filterName) => ({
    filterDropdown: ({
      setSelectedKeys,
      selectedKeys,
      confirm,
      clearFilters,
    }) => (
      <div style={{ padding: 8 }} className={filterName}>
        <div style={{ marginBottom: 8, display: "block" }}>
          <RangePicker
            value={selectedKeys[0]}
            onChange={(dates) => {
              if (dates !== null) {
                setSelectedKeys([
                  [dates[0].startOf("day"), dates[1].endOf("day")],
                ]);
                confirm({ closeDropdown: false });
              } else {
                handleClearSearch(clearFilters, confirm);
              }
            }}
          />
        </div>
        <Space>
          <Button
            disabled={selectedKeys.length === 0}
            onClick={() => handleClearSearch(clearFilters, confirm)}
            size="small"
            style={{ width: 89 }}
            className="t-clear-btn"
          >
            {i18n("Filter.clear")}
          </Button>
          <Button
            type="primary"
            onClick={() => handleSearch(selectedKeys, confirm)}
            icon={<IconSearch />}
            size="small"
            style={{ width: 90 }}
            className="t-search-btn"
          >
            {i18n("Filter.search")}
          </Button>
        </Space>
      </div>
    ),
    filterIcon: (filtered) => (
      <IconSearch style={{ color: filtered ? blue6 : undefined }} />
    ),
  });

  const columns = [
    {
      title: () => {
        const indeterminate = selectedCount < total && selectedCount > 0;
        return (
          <Checkbox
            className="t-select-all"
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
              onChange={(e) => onRowSelectionChange(e, item)}
              checked={selected[item.key]}
            />
            <SampleIcons sample={item} />
          </Space>
        );
      },
    },
    {
      title: i18n("SamplesTable.Column.sampleName"),
      className: "t-td-name",
      dataIndex: ["sample", "sampleName"],
      sorter: true,
      render: (name, row) => (
        <SampleDetailViewer sampleId={row.sample.id} projectId={row.project.id}>
          <Button type="link" className="t-sample-name" style={{ padding: 0 }}>
            {name}
          </Button>
        </SampleDetailViewer>
      ),
      ...getColumnSearchProps(
        ["sample", "sampleName"],
        "t-name-select",
        i18n("Filter.sampleName.placeholder")
      ),
    },
    {
      title: i18n("SamplesTable.Column.quality"),
      width: 100,
      dataIndex: "qcStatus",
      render: (qcStatus, row) => (
        <SampleQuality qcStatus={qcStatus} qualities={row.quality} />
      ),
    },
    {
      title: i18n("SamplesTable.Column.coverage"),
      className: "t-td-coverage",
      width: 100,
      dataIndex: "coverage",
    },
    {
      title: i18n("SamplesTable.Column.organism"),
      className: "t-td-organism",
      dataIndex: ["sample", "organism"],
      sorter: true,
      ...getColumnSearchProps(["sample", "organism"], "t-organism-select"),
    },
    {
      title: i18n("SamplesTable.Column.project"),
      className: "t-td-project",
      dataIndex: ["project", "name"],
      sorter: true,
      key: "associated",
      render: (name, row) => {
        return <ProjectTag project={row.project} />;
      },
      filters: associatedProjects,
    },
    {
      title: i18n("SamplesTable.Column.collectedBy"),
      dataIndex: ["sample", "collectedBy"],
      sorter: true,
      ...getColumnSearchProps(["sample", "collectedBy"]),
    },
    {
      title: i18n("SamplesTable.Column.created"),
      className: "t-td-created",
      dataIndex: ["sample", "createdDate"],
      sorter: true,
      width: 230,
      render: (createdDate) => {
        return formatInternationalizedDateTime(createdDate);
      },
      ...getDateColumnSearchProps("t-created-filter"),
    },
    {
      title: i18n("SamplesTable.Column.modified"),
      className: "t-td-modified",
      dataIndex: ["sample", "modifiedDate"],
      defaultSortOrder: "descend",
      sorter: true,
      width: 230,
      render: (modifiedDate) => {
        return formatInternationalizedDateTime(modifiedDate);
      },
      ...getDateColumnSearchProps("t-modified-filter"),
    },
  ];

  return (
    <Table
      className="t-samples-table"
      loading={isFetching || loadingLong}
      columns={columns}
      dataSource={samples}
      pagination={getPaginationOptions(total)}
      onChange={onTableChange}
      summary={() => (
        <Table.Summary.Row>
          <Table.Summary.Cell colSpan={5} className="t-summary">
            {i18n("SamplesTable.Summary", selectedCount, total)}
          </Table.Summary.Cell>
        </Table.Summary.Row>
      )}
    />
  );
}

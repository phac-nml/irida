import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { FolderAddOutlined } from "@ant-design/icons";
import { Checkbox, Space, Table, Tag, Tooltip } from "antd";
import { useListAssociatedProjectsQuery } from "../../../../apis/projects/associated-projects";
import { blue6 } from "../../../../styles/colors";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { formatSort } from "../../../../utilities/table-utilities";
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

/**
 * React element to render a table display samples belong to a project,
 * and the project's associated projects.
 * @returns {JSX.Element}
 * @constructor
 */
export function SamplesTable() {
  const dispatch = useDispatch();
  const { projectId, options, selected } = useSelector(state => state.samples);

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

  // const [colors, setColors] = React.useState(() => {
  //   const colorString = localStorage.getItem("projectColors");
  //   return colorString ? JSON.parse(colorString) : {};
  // });

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
  const updateSelectAll = e =>
    e.target.checked
      ? dispatch(selectAllSamples(projectId, options))
      : dispatch(clearSelectedSamples());

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
        filters,
        pagination,
        order: formatSort(sorter)
      })
    );

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
              onChange={e => onRowSelectionChange(e, item)}
              checked={selected[item.key]}
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
    <Table
      loading={isFetching}
      columns={columns}
      dataSource={samples}
      pagination={{ ...options.pagination, total }}
      onChange={onTableChange}
      summary={() => (
        <Table.Summary.Row>
          <Table.Summary.Cell colSpan={5}>
            {`Selected: ${Object.keys(selected).length} of
                ${total}`}
          </Table.Summary.Cell>
        </Table.Summary.Row>
      )}
    />
  );
}

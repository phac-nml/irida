import React, { useContext, useEffect, useState } from "react";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { Button, Popconfirm, Table } from "antd";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../ant.design/table-renderers";
import {
  deleteAnalysisSubmissions,
  fetchAllPipelinesStates,
  fetchAllPipelinesTypes
} from "../../apis/analysis/analysis";
import { AnalysisState } from "./AnalysisState";
import { getHumanizedDuration } from "../../utilities/date-utilities.js";
import { getTextSearchProps } from "../ant.design/table-search-props";
import { blue6, grey6 } from "../../styles/colors";
import { SPACE_MD } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AnalysesQueue } from "./../AnalysesQueue";
import { DownloadOutlined } from "@ant-design/icons";
import { FilterIcon } from "../Tables/fitlers/FilterIcon";
import styled from "styled-components";

const DownloadButton = styled(Button)`
  color: ${grey6};

  &:hover {
    color: ${blue6};
  }
`;

/**
 * Displays the Analyses Table for both user and admin pages.
 * @returns {*}
 * @constructor
 */
export function AnalysesTable() {
  const CAN_MANAGE = window.PAGE.canManage;
  const {
    loading,
    total,
    pageSize,
    dataSource,
    handleTableChange,
    updateTable
  } = useContext(PagedTableContext);

  /**
   * Handler for deleting an analysis.
   *
   * @param {array} ids
   * @returns {void | Promise<*>}
   */
  const deleteAnalyses = ids =>
    deleteAnalysisSubmissions({ ids }).then(updateTable);

  const [selected, setSelected] = useState([]);
  const [pipelineStates, setPipelineStates] = useState([]);
  const [pipelineTypes, setPipelineTypes] = useState([]);
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    Promise.all([fetchAllPipelinesStates(), fetchAllPipelinesTypes()]).then(
      ([states, types]) => {
        setPipelineStates(states);
        setPipelineTypes(types);
      }
    );
  }, []);

  const columns = [
    {
      ...nameColumnFormat({
        url: setBaseUrl(`analysis/`)
      }),
      title: i18n("analyses.analysis-name"),
      key: "name",
      ...getTextSearchProps("name")
    },
    {
      title: i18n("analyses.state"),
      key: "state",
      dataIndex: "state",
      filterMultiple: true,
      filters: pipelineStates,
      filterIcon(filtered) {
        return <FilterIcon filtered={filtered} />;
      },
      render(state) {
        return <AnalysisState state={state} />;
      }
    },
    {
      title: i18n("analyses.type"),
      key: "type",
      width: 250,
      dataIndex: "type",
      filterMultiple: true,
      filterIcon(filtered) {
        return <FilterIcon filtered={filtered} />;
      },
      filters: pipelineTypes
    },
    {
      title: i18n("analyses.submitter"),
      width: 200,
      key: "submitter",
      sorter: true,
      dataIndex: "submitter"
    },
    {
      ...dateColumnFormat(),
      title: "Created Date",
      dataIndex: "createdDate",
      key: "createdDate"
    },
    {
      title: i18n("analysis.duration"),
      key: "duration",
      dataIndex: "duration",
      render(timestamp) {
        return getHumanizedDuration({ date: timestamp });
      }
    },
    {
      title: "",
      key: "download",
      fixed: "right",
      align: "right",
      width: 60,
      render(text, record) {
        return (
          <DownloadButton
            shape="circle-outline"
            disabled={record.state.value !== "COMPLETED"}
            href={setBaseUrl(`ajax/analyses/download/${record.id}`)}
            download
          >
            <DownloadOutlined />
          </DownloadButton>
        );
      }
    }
  ];

  let rowSelection;
  if (CAN_MANAGE) {
    rowSelection = {
      selectedRowKeys: selected,
      onChange: selectedRowKeys => setSelected(selectedRowKeys),
      getCheckboxProps: record => ({
        name: record.name,
        disabled: !record.modifiable
      })
    };
  }

  return (
    <div>
      <div
        style={{
          marginBottom: SPACE_MD,
          display: "flex"
        }}
      >
        <div style={{ flex: 1 }}>
          <Popconfirm
            placement="bottomRight"
            title={i18n("analyses.delete-confirm").replace(
              "[COUNT]",
              selected.length
            )}
            onVisibleChange={visible => setDeleting(visible)}
            onConfirm={() =>
              deleteAnalyses(selected).then(() => setSelected([]))
            }
          >
            <Button
              className="t-delete-selected"
              loading={deleting}
              disabled={!selected.length}
              onClick={() => setDeleting(true)}
            >
              {i18n("analyses.delete")}
            </Button>
          </Popconfirm>
        </div>
        <AnalysesQueue />
      </div>
      <Table
        rowSelection={rowSelection}
        scroll={{ x: "max-content" }}
        rowKey={record => record.id}
        loading={loading}
        pagination={{ total, pageSize }}
        columns={columns}
        dataSource={dataSource}
        onChange={handleTableChange}
      />
    </div>
  );
}

AnalysesTable.propTypes = {};

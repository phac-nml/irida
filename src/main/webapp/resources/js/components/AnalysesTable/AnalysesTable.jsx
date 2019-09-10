import React, { useContext, useEffect, useState } from "react";
import { AnalysesContext } from "../../contexts/AnalysesContext";
import { Button, Icon, Row, Table } from "antd";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../ant.design/table-renderers";
import {
  fetchAllPipelinesStates,
  fetchAllPipelinesTypes
} from "../../apis/analysis/analysis";
import { AnalysisState } from "./AnalysisState";
import { getI18N } from "../../utilities/i18n-utilties";
import { getHumanizedDuration } from "../../utilities/date-utilities.js";
import { getTextSearchProps } from "../ant.design/table-search-props";
import { blue6 } from "../../styles/colors";

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
    analyses,
    handleTableChange,
    deleteAnalyses
  } = useContext(AnalysesContext);

  const [selected, setSelected] = useState([]);
  const [pipelineStates, setPipelineStates] = useState([]);
  const [pipelineTypes, setPipelineTypes] = useState([]);

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
        url: `${window.TL.BASE_URL}analysis/`,
        width: 300
      }),
      title: getI18N("analyses.analysis-name"),
      key: "name",
      ...getTextSearchProps("name")
    },
    {
      title: getI18N("analyses.state"),
      key: "state",
      dataIndex: "state",
      filterMultiple: true,
      filters: pipelineStates,
      width: 150,
      filterIcon(filtered) {
        return (
          <Icon
            type="filter"
            theme="filled"
            style={{ color: filtered ? blue6 : undefined }}
            className="t-state"
          />
        );
      },
      render(state) {
        return <AnalysisState state={state} />;
      }
    },
    {
      title: getI18N("analyses.type"),
      key: "type",
      width: 250,
      dataIndex: "type",
      filterMultiple: true,
      filterIcon(filtered) {
        return (
          <Icon
            type="filter"
            theme="filled"
            style={{ color: filtered ? blue6 : undefined }}
            className="t-type"
          />
        );
      },
      filters: pipelineTypes
    },
    {
      title: getI18N("analyses.submitter"),
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
      title: getI18N("analysis.duration"),
      key: "duration",
      width: 150,
      dataIndex: "duration",
      render(timestamp) {
        return getHumanizedDuration({ date: timestamp });
      }
    },
    {
      title: "",
      key: "download",
      fixed: "right",
      render(text, record) {
        return (
          <Button
            shape="circle-outline"
            disabled={record.state.value !== "COMPLETED"}
            href={`${window.TL.BASE_URL}ajax/analyses/download/${record.id}`}
            download
            icon="download"
          />
        );
      }
    }
  ];

  let rowSelection;
  if (CAN_MANAGE) {
    rowSelection = {
      onChange: selectedRowKeys => setSelected(selectedRowKeys),
      getCheckboxProps: record => ({ name: record.name })
    };
  }

  return (
    <div>
      <div>
        <Button
          onClick={() => deleteAnalyses(selected)}
          disabled={!selected.length}
        >
          DELETE
        </Button>
      </div>
      <Table
        rowSelection={rowSelection}
        scroll={{ x: "max-content" }}
        rowKey={record => record.id}
        loading={loading}
        pagination={{ total, pageSize }}
        columns={columns}
        dataSource={analyses}
        onChange={handleTableChange}
      />
    </div>
  );
}

AnalysesTable.propTypes = {};

import React, { useContext } from "react";
import { AnalysesContext } from "../../contexts/AnalysesContext";
import { Button, Icon, Popconfirm, Table } from "antd";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../ant.design/table-renderers";
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
    types,
    pipelineStates,
    handleTableChange,
    deleteAnalysis
  } = useContext(AnalysesContext);

  function createColumns({ types, pipelineStates, deleteAnalysis }) {
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
        render(state, data) {
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
        filters: types
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

    if (CAN_MANAGE) {
      columns.push({
        title: "",
        key: "delete",
        fixed: "right",
        render(text, record) {
          return record.modifiable ? (
            <Popconfirm
              placement={"top"}
              An
              title={"Delete this analysis?"}
              onConfirm={() => deleteAnalysis(record.id)}
            >
              <Button
                shape="circle-outline"
                className="t-delete-btn"
                icon="delete"
              />
            </Popconfirm>
          ) : (
            <span />
          );
        }
      });
    }

    return columns;
  }

  return (
    <Table
      scroll={{ x: "max-content" }}
      rowKey={record => record.id}
      loading={loading}
      pagination={{ total, pageSize }}
      columns={createColumns({ types, pipelineStates, deleteAnalysis })}
      dataSource={analyses}
      onChange={handleTableChange}
    />
  );
}

AnalysesTable.propTypes = {};

import React, { useContext } from "react";
import { AnalysesContext } from "../../contexts/AnalysesContext";
import { Button, Icon, Popconfirm, Table } from "antd";
import { PageWrapper } from "../page/PageWrapper";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../ant.design/table-renderers";
import { AnalysisState } from "./AnalysisState";
import { getI18N } from "../../utilities/i18n-utilties";
import { getHumanizedDuration } from "../../utilities/date-utilities.js";
import { getTextSearchProps } from "../ant.design/table-search-props";

/**
 * Displays the Analyses Table for both user and admin pages.
 * @returns {*}
 * @constructor
 */
export function AnalysesTable() {
  const ADMIN = window.location.href.endsWith("all");
  const {
    loading,
    total,
    pageSize,
    analyses,
    types,
    pipelineStates,
    handleTableChange,
    deleteAnalysis,
    downloadAnalysis
  } = useContext(AnalysesContext);

  function createColumns({ types, pipelineStates, deleteAnalysis }) {
    const columns = [
      {
        ...nameColumnFormat({
          url: `${window.TL.BASE_URL}analysis/`,
          width: 300
        }),
        filterIcon(filtered) {
          return (
            <Icon
              type="filter"
              theme="filled"
              style={{ color: filtered ? "#1890ff" : undefined }}
              className="t-name"
            />
          );
        },
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
        filterIcon(filtered) {
          return (
            <Icon
              type="filter"
              theme="filled"
              style={{ color: filtered ? "#1890ff" : undefined }}
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
              style={{ color: filtered ? "#1890ff" : undefined }}
              className="t-type"
            />
          );
        },
        filters: types
      },
      {
        title: getI18N("analyses.submitter"),
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
        dataIndex: "duration",
        render(timestamp) {
          return getHumanizedDuration({ date: timestamp });
        }
      }
    ];

    if (ADMIN) {
      columns.push({
        title: "",
        key: "actions",
        fixed: "right",
        render(text, record) {
          return record.modifiable ? (
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center"
              }}
            >
              {record.state === "Error" ? (
                <span />
              ) : (
                <Button
                  type={"link"}
                  onClick={() => downloadAnalysis(record.id)}
                >
                  <Icon type="download" />
                </Button>
              )}
              <Popconfirm
                placement={"top"}
                title={"Delete this analysis?"}
                onConfirm={() => deleteAnalysis(record.id)}
              >
                <Button type="link" className="t-delete-btn">
                  <Icon type="delete" theme="twoTone" />
                </Button>
              </Popconfirm>
            </div>
          ) : null;
        }
      });
    }

    return columns;
  }

  return (
    <PageWrapper title={getI18N("analyses.header")}>
      <Table
        scroll={{ x: "max-content" }}
        rowKey={record => record.id}
        loading={loading}
        pagination={{ total, pageSize }}
        columns={createColumns({ types, pipelineStates, deleteAnalysis })}
        dataSource={analyses}
        onChange={handleTableChange}
      />
    </PageWrapper>
  );
}

AnalysesTable.propTypes = {};

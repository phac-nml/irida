import React, { useContext } from "react";
import { AnalysesContext } from "../../contexts/AnalysesContext";
import { Button, Input, Popconfirm, Row, Table } from "antd";
import { PageWrapper } from "../../components/page/PageWrapper";
import {
  dateColumnFormat,
  nameColumnFormat
} from "../../components/ant.design/table-renderers";
import { AnalysisState } from "./AnalysisState";
import { getI18N } from "./../../utilities/i18n-utilties";
import { getHumanizedDuration } from "./../../utilities/date-utilities.js";
import { getTextSearchProps } from "../../components/ant.design/table-search-props";

export function AnalysesTable() {
  const ADMIN = window.location.href.endsWith("all");
  const {
    loading,
    total,
    pageSize,
    analyses,
    types,
    pipelineStates,
    onSearch,
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
        width: 130,
        filterMultiple: false,
        filters: pipelineStates,
        render(state, data) {
          return <AnalysisState state={state} percentage={data.percentage} />;
        }
      },
      {
        title: getI18N("analyses.type"),
        key: "type",
        width: 250,
        dataIndex: "type",
        filterMultiple: false,
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
        render: (text, record) => (
          <Popconfirm
            placement={"top"}
            title={"Delete this analysis?"}
            onConfirm={() => deleteAnalysis(record.id)}
          >
            <Button type={"link"} size="small">
              Delete
            </Button>
          </Popconfirm>
        )
      });
    }

    return columns;
  }

  return (
    <PageWrapper
      title={getI18N("analyses.header")}
      headerExtras={
        <Row gutter={12} style={{ marginRight: 18 }}>
          <Input.Search onSearch={onSearch} />
        </Row>
      }
    >
      <Table
        style={{ margin: "6px 24px 0 24px" }}
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

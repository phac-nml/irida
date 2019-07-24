import React, { useEffect, useReducer, useState } from "react";
import { AnalysesConsumer } from "../../contexts/AnalysesContext";
import { Input, Row, Table } from "antd";
import { PageWrapper } from "../../components/page/PageWrapper";
import {
  dateColumnFormat,
  idColumnFormat,
  nameColumnFormat
} from "../../components/ant.design/table-renderers";
import { AnalysisState } from "./AnalysisState";
import { getI18N } from "./../../utilities/i18n-utilties";
import { getHumanizedDuration } from "./../../utilities/date-utilities.js";

export function AnalysesTable() {
  function createColumns({ types, pipelineStates }) {
    return [
      {
        ...idColumnFormat(),
        title: getI18N("analyses.id")
      },
      {
        ...nameColumnFormat(`${window.TL.BASE_URL}analysis/`),
        title: getI18N("analyses.analysis-name"),
        key: "name"
      },
      {
        title: getI18N("analyses.state"),
        key: "state",
        dataIndex: "state",
        width: 100,
        filterMultiple: false,
        filters: pipelineStates,
        render(state, data) {
          return <AnalysisState state={state} percentage={data.percentage} />;
        }
      },
      {
        title: getI18N("analyses.type"),
        key: "type",
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
  }

  return (
    <AnalysesConsumer>
      {({
        loading,
        total,
        pageSize,
        analyses,
        types,
        pipelineStates,
        onSearch,
        handleTableChange
      }) => (
        <PageWrapper
          title={"__ANALYSES__"}
          headerExtras={
            <Row gutter={12} style={{ marginRight: 18 }}>
              <Input.Search onSearch={onSearch} />
            </Row>
          }
        >
          <Table
            style={{ margin: "6px 24px 0 24px" }}
            scroll={{ x: 900 }}
            rowKey={record => record.id}
            loading={loading}
            pagination={{ total, pageSize }}
            columns={createColumns({ types, pipelineStates })}
            dataSource={analyses}
            onChange={handleTableChange}
          />
        </PageWrapper>
      )}
    </AnalysesConsumer>
  );
}

AnalysesTable.propTypes = {};

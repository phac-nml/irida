import React from "react";
import { Input, Space, Table } from "antd";

const { Search } = Input;
/**
 * React component to display sample analyses
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAnalyses() {
  // Columns for the table
  const columns = [
    {
      title: i18n("SampleAnalyses.sampleId"),
      key: "sample_id",
    },
    {
      title: i18n("SampleAnalyses.sampleName"),
      key: "sample_name",
    },
    {
      title: i18n("SampleAnalyses.analysisSubmission"),
      key: "analysis_name",
    },
    {
      title: i18n("SampleAnalyses.analysisType"),
      key: "analysis_type",
    },
  ];

  return (
    <Space style={{ display: "block" }} direction="vertical">
      <Search
        placeholder={i18n("SampleAnalyses.inputSearchText")}
        onChange={(e) => console.log(e.target.value)}
        allowClear={true}
        className="t-sample-search-input"
      />
      <Table bordered columns={columns} className="t-sample-analyses" />
    </Space>
  );
}

import React from "react";
import { Input, Table } from "antd";
import { SPACE_MD } from "../../../styles/spacing";

const { Search } = Input;
/**
 * React component to display basic sample information
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
    <>
      <Search
        placeholder={i18n("SampleAnalyses.inputSearchText")}
        onChange={(e) => console.log(e.target.value)}
        style={{ width: "100%", marginBottom: SPACE_MD }}
        allowClear={true}
        className="t-sample-search-input"
      />
      <Table bordered columns={columns} className="t-sample-analyses" />
    </>
  );
}

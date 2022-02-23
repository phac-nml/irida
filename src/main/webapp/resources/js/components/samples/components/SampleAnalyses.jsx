import React from "react";
import { Input, Space, Table } from "antd";
import { useDispatch, useSelector } from "react-redux";
import { fetchSampleAnalyses } from "../../../apis/samples/samples";
import { setSampleAnalyses } from "../sampleAnalysesSlice";

import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import {
  IconCheckCircle,
  IconClock,
  IconCloseCircle,
  IconLoading,
} from "../../icons/Icons";
import { blue6, green6, grey6, red6 } from "../../../styles/colors";

const { Search } = Input;

const commonIconStyle = {
  fontSize: "16px",
};

/**
 * React component to display sample analyses
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAnalyses() {
  const [filteredSubmissions, setFilteredSubmissions] = React.useState(null);
  const dispatch = useDispatch();

  const { sample } = useSelector((state) => state.sampleReducer);

  const { analyses, loading } = useSelector(
    (state) => state.sampleAnalysesReducer
  );

  /*
  On page load get the sample analyses
   */
  React.useEffect(() => {
    fetchSampleAnalyses({ sampleId: sample.identifier })
      .then((analysesList) => {
        dispatch(setSampleAnalyses({ analyses: analysesList }));
      })
      .catch((error) => {});
  }, []);

  // Columns for the table
  const columns = [
    {
      title: i18n("SampleAnalyses.analysisSubmissionName"),
      dataIndex: "name",
      key: "name",
      render(name, data) {
        return (
          <a
            className="t-analysis-name"
            href={setBaseUrl(`analysis/${data.id}`)}
            title={name}
            target="_blank"
          >
            {name}
          </a>
        );
      },
    },
    {
      title: i18n("SampleAnalyses.analysisType"),
      dataIndex: "analysisType",
      key: "analysisType",
    },
    {
      title: i18n("SampleAnalyses.createdDate"),
      dataIndex: "createdDate",
      key: "createdDate",
      width: 230,
      render: (date) => (
        <span className="t-analysis-created-date">
          {date ? formatInternationalizedDateTime(date) : ""}
        </span>
      ),
    },
    {
      title: i18n("SampleAnalyses.analysisState"),
      dataIndex: "state",
      key: "analysisType",
      render: (state) => (
        <span className="t-analysis-state">
          {state === "COMPLETED" ? (
            <IconCheckCircle style={{ ...commonIconStyle, color: green6 }} />
          ) : state === "ERROR" ? (
            <IconCloseCircle style={{ ...commonIconStyle, color: red6 }} />
          ) : state === "QUEUED" ? (
            <IconClock style={{ ...commonIconStyle, color: grey6 }} />
          ) : (
            <IconLoading style={{ ...commonIconStyle, color: blue6 }} />
          )}
        </span>
      ),
    },
  ];

  /*
  Filter displayed submissions by name or type
   */
  const searchSubmissions = (searchStr) => {
    if (
      searchStr.trim() === "" ||
      searchStr === "undefined" ||
      searchStr === null
    ) {
      setFilteredSubmissions(analyses);
    } else {
      searchStr = String(searchStr).toLowerCase();
      const submissionsContainingSearchValue = analyses.filter(
        (submission) =>
          submission.name.toLowerCase().includes(searchStr) ||
          submission.analysisType.toLowerCase().includes(searchStr)
      );

      setFilteredSubmissions(submissionsContainingSearchValue);
    }
  };

  return (
    <Space direction="vertical" size="large" style={{ width: `100%` }}>
      <Search
        placeholder={i18n("SampleAnalyses.inputSearchText")}
        onChange={(e) => searchSubmissions(e.target.value)}
        allowClear={true}
        className="t-sample-search-input"
      />
      <Table
        bordered
        columns={columns}
        loading={loading}
        dataSource={
          filteredSubmissions !== null ? filteredSubmissions : analyses
        }
        rowKey={(item) => `analysis-submission-${item.id}`}
        className="t-sample-analyses"
      />
    </Space>
  );
}

import React from "react";
import { Col, Input, Row, Table } from "antd";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { fetchSampleAnalyses } from "../../../apis/samples/samples";
import { setSampleAnalyses } from "../sampleAnalysesSlice";
import { SampleAnalysesState } from "./SampleAnalysesState";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import {
  AnalysisState,
  SampleAnalyses as SampleAnalysesItem,
} from "../../../apis/samples/samples";

const { Search } = Input;

/**
 * React component to display sample analyses
 *
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleAnalyses() {
  const [filteredSubmissions, setFilteredSubmissions] = React.useState<
    SampleAnalysesItem[]
  >([]);
  const [allAnalysesDisplayed, setAllAnalysesDisplayed] =
    React.useState<boolean>(true);

  const dispatch = useAppDispatch();

  const { sample } = useAppSelector((state) => state.sampleReducer);

  const { analyses, loading } = useAppSelector(
    (state) => state.sampleAnalysesReducer
  );

  /*
  On page load get the sample analyses
   */
  React.useEffect(() => {
    fetchSampleAnalyses({ sampleId: sample.identifier }).then(
      (analysesList) => {
        dispatch(setSampleAnalyses({ analyses: analysesList }));
      }
    );
  }, []);

  // Columns for the table
  const columns = [
    {
      title: i18n("SampleAnalyses.analysisSubmissionName"),
      dataIndex: "name",
      key: "name",
      ellipsis: true,
      width: 250,
      render(name: string, data: SampleAnalysesItem) {
        return (
          <a
            className="t-analysis-name"
            href={setBaseUrl(`analysis/${data.id}`)}
            title={name}
            target="_blank"
            rel="noreferrer"
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
      width: 150,
    },
    {
      title: i18n("SampleAnalyses.createdDate"),
      dataIndex: "createdDate",
      key: "createdDate",
      width: 200,
      render: (date: Date) => (
        <span className="t-analysis-created-date">
          {date ? formatInternationalizedDateTime(date) : ""}
        </span>
      ),
    },
    {
      title: i18n("SampleAnalyses.analysisState"),
      dataIndex: "state",
      key: "analysisType",
      width: 100,
      render: (state: AnalysisState) => <SampleAnalysesState state={state} />,
    },
  ];

  /*
  Filter displayed submissions by name or type
   */
  const searchSubmissions = (searchStr: string) => {
    if (
      searchStr.trim() === "" ||
      searchStr === "undefined" ||
      searchStr === null
    ) {
      setFilteredSubmissions(analyses);
    } else {
      searchStr = String(searchStr).toLowerCase();
      const submissionsContainingSearchValue: SampleAnalysesItem[] =
        analyses.filter(
          (submission: SampleAnalysesItem) =>
            submission.name.toLowerCase().includes(searchStr) ||
            submission.analysisType.toLowerCase().includes(searchStr)
        );
      if (submissionsContainingSearchValue.length >= 0) {
        setAllAnalysesDisplayed(false);
      }
      setFilteredSubmissions(submissionsContainingSearchValue);
    }
  };

  return (
    <Row gutter={[16, 16]}>
      <Col span={24}>
        <Search
          placeholder={i18n("SampleAnalyses.inputSearchText")}
          onChange={(e) => searchSubmissions(e.target.value)}
          allowClear={true}
          className="t-sample-analyses-search-input"
        />
      </Col>
      <Col span={24}>
        <Table
          columns={columns}
          loading={loading}
          dataSource={
            !allAnalysesDisplayed && filteredSubmissions.length >= 0
              ? filteredSubmissions
              : analyses
          }
          rowKey={(item) => `analysis-submission-${item.id}`}
          className="t-sample-analyses"
        />
      </Col>
    </Row>
  );
}

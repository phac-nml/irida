import React from "react";
import { Avatar, Col, Input, List, Row } from "antd";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import {
  fetchSampleAnalyses,
  SampleAnalyses as SampleAnalysesItem,
} from "../../../apis/samples/samples";
import { setSampleAnalyses } from "../sampleAnalysesSlice";
import { SampleAnalysesState } from "./SampleAnalysesState";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import AutoSizer from "react-virtualized-auto-sizer";
import { FixedSizeList as VList } from "react-window";
import { HEADER_HEIGHT } from "./ViewerHeader";

const { Search } = Input;

/**
 * React component to display sample analyses
 *
 * @returns {JSX.Element}
 * @constructor
 */
export default function SampleAnalyses() {
  const [filteredAnalyses, setFilteredAnalyses] = React.useState<
    SampleAnalysesItem[]
  >([]);

  const dispatch = useAppDispatch();

  const { sample } = useAppSelector((state) => state.sampleReducer);

  const { analyses } = useAppSelector((state) => state.sampleAnalysesReducer);

  React.useEffect(() => setFilteredAnalyses(analyses), [analyses]);

  /*
  On page load get the sample analyses
   */
  React.useEffect(() => {
    fetchSampleAnalyses({ sampleId: sample.identifier }).then(
      (analysesList) => {
        dispatch(setSampleAnalyses({ analyses: analysesList }));
      }
    );
  }, [dispatch, sample.identifier]);

  /*
  Filter displayed submissions by name or type
   */
  const searchSubmissions = (searchStr: string) => {
    if (
      searchStr.trim() === "" ||
      searchStr === "undefined" ||
      searchStr === null
    ) {
      setFilteredAnalyses(analyses);
    } else {
      searchStr = String(searchStr).toLowerCase();
      const submissionsContainingSearchValue: SampleAnalysesItem[] =
        analyses.filter(
          (submission: SampleAnalysesItem) =>
            submission.name.toLowerCase().includes(searchStr) ||
            submission.analysisType.toLowerCase().includes(searchStr)
        );
      setFilteredAnalyses(submissionsContainingSearchValue);
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
      <Col
        span={24}
        style={{
          height: `calc(80vh - ${HEADER_HEIGHT}px - 48px)`,
        }}
      >
        <AutoSizer>
          {({ height, width = "100%" }) => (
            <VList
              itemCount={filteredAnalyses.length}
              itemSize={70}
              width={width}
              height={height}
              style={{
                border: `1px solid var(--grey-4)`,
              }}
            >
              {({ index, style }) => {
                const item = filteredAnalyses[index];
                return (
                  <List.Item
                    extra={[
                      <span
                        key={`date-${item.id}`}
                        className="t-analysis-created-date"
                        style={{ padding: 10 }}
                      >
                        {item.createdDate
                          ? formatInternationalizedDateTime(item.createdDate)
                          : ""}
                      </span>,
                    ]}
                    style={{
                      ...style,
                      borderBottom: `1px solid var(--grey-4)`,
                    }}
                  >
                    <List.Item.Meta
                      avatar={
                        <Avatar
                          style={{ backgroundColor: `var(--grey-1)` }}
                          icon={<SampleAnalysesState state={item.state} />}
                        />
                      }
                      title={
                        <a
                          className="t-analysis-name"
                          href={setBaseUrl(`analysis/${item.id}`)}
                          target="_blank"
                          rel="noreferrer"
                        >
                          {item.name}
                        </a>
                      }
                      description={item.analysisType}
                    />
                  </List.Item>
                );
              }}
            </VList>
          )}
        </AutoSizer>
      </Col>
    </Row>
  );
}

/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext, useState, useLayoutEffect } from "react";

import { AnalysisSamplesContext } from "../../../../contexts/AnalysisSamplesContext";
import { Avatar, Icon, Input, List } from "antd";
import { SPACE_MD } from "../../../../styles/spacing";
import { InfoAlert } from "../../../../components/alerts/InfoAlert";
import { ContentLoading } from "../../../../components/loader/ContentLoading";

const { Search } = Input;

export function AnalysisSampleRenderer() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const {
    analysisSamplesContext,
    sampleDisplayHeight,
    getAnalysisInputSamples
  } = useContext(AnalysisSamplesContext);

  useLayoutEffect(() => {
    if (analysisSamplesContext.samples === null) {
      getAnalysisInputSamples();
    }
  }, []);

  const [filteredSamples, setFilteredSamples] = useState(null);

  const renderSamples = () => {
    return (
      <List
        bordered
        dataSource={
          filteredSamples !== null
            ? filteredSamples
            : analysisSamplesContext.samples
        }
        style={{ maxHeight: sampleDisplayHeight, overflowY: "auto" }}
        renderItem={item => {
          return (
            <List.Item>
              <List.Item.Meta
                key={item.sampleId}
                avatar={
                  <Avatar>
                    <Icon type="experiment" />
                  </Avatar>
                }
                title={
                  <a
                    href={`${window.TL.BASE_URL}samples/${item.sampleId}/details`}
                    target="_blank"
                  >
                    {item.sampleName}
                  </a>
                }
                description={
                  <div>
                    <div key={`file-${item.forward.identifier}`}>
                      <a
                        href={`${window.TL.BASE_URL}sequenceFiles/${item.sequenceFilePairId}/file/${item.forward.identifier}/summary`}
                        target="_blank"
                      >
                        {item.forward.fileName}
                      </a>
                    </div>
                    <div key={`file-${item.reverse.identifier}`}>
                      <a
                        href={`${window.TL.BASE_URL}sequenceFiles/${item.sequenceFilePairId}/file/${item.reverse.identifier}/summary`}
                        target="_blank"
                      >
                        {item.reverse.fileName}
                      </a>
                    </div>
                  </div>
                }
              />
            </List.Item>
          );
        }}
      />
    );
  };

  /*
   * if search value is empty display all the samples otherwise
   * find samples with sample name or files that contain the search string
   */
  const searchSamples = searchStr => {
    if (
      searchStr.trim() === "" ||
      searchStr === "undefined" ||
      searchStr === null
    ) {
      setFilteredSamples(analysisSamplesContext.samples);
    } else {
      searchStr = String(searchStr).toLowerCase();
      const samplesContainingSearchValue = analysisSamplesContext.samples.filter(
        sample =>
          sample.sampleName.toLowerCase().includes(searchStr) ||
          sample.forward.fileName.toLowerCase().includes(searchStr) ||
          sample.reverse.fileName.toLowerCase().includes(searchStr)
      );
      setFilteredSamples(samplesContainingSearchValue);
    }
  };

  return (
    <>
      {analysisSamplesContext.loading ? (
        <div>
          <ContentLoading
            message={i18n("AnalysisSamples.checkingForSamples")}
          />
        </div>
      ) : analysisSamplesContext.samples.length > 0 ? (
        <div>
          <Search
            placeholder={i18n("AnalysisSamples.searchSamples")}
            onChange={event => searchSamples(event.target.value)}
            style={{ width: "100%", marginBottom: SPACE_MD }}
            allowClear={true}
          />
          {renderSamples()}
        </div>
      ) : (
        <InfoAlert message={i18n("AnalysisSamples.samplesDeleted")} />
      )}
    </>
  );
}

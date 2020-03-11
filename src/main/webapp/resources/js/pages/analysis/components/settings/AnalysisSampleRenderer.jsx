/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext, useLayoutEffect, useState } from "react";

import { AnalysisSamplesContext } from "../../../../contexts/AnalysisSamplesContext";
import { Avatar, Input, List } from "antd";
import { SPACE_MD } from "../../../../styles/spacing";
import { InfoAlert } from "../../../../components/alerts/InfoAlert";
import { ContentLoading } from "../../../../components/loader/ContentLoading";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconExperiment } from "../../../../components/icons/Icons";

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
  const [filteredSingleEndSamples, setSingleEndFilteredSamples] = useState(
    null
  );
  const SEQ_FILES_BASE_URL = setBaseUrl("sequenceFiles");
  const SAMPLES_BASE_URL = setBaseUrl("samples");

  const renderPairedEndSamples = () => {
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
                className="t-paired-end"
                avatar={<Avatar icon={<IconExperiment />} />}
                title={
                  <a
                    href={`${SAMPLES_BASE_URL}/${item.sampleId}/details`}
                    target="_blank"
                    className="t-paired-end-sample-name"
                  >
                    {item.sampleName}
                  </a>
                }
                description={
                  <div>
                    <div key={`file-${item.forward.identifier}`}>
                      <a
                        href={`${SEQ_FILES_BASE_URL}/${item.sequenceFilePairId}/file/${item.forward.identifier}/summary`}
                        target="_blank"
                      >
                        {item.forward.fileName}
                      </a>
                    </div>
                    <div key={`file-${item.reverse.identifier}`}>
                      <a
                        href={`${SEQ_FILES_BASE_URL}/${item.sequenceFilePairId}/file/${item.reverse.identifier}/summary`}
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

  const renderSingleEndSamples = () => {
    return (
      <List
        bordered
        dataSource={
          filteredSamples !== null
            ? filteredSingleEndSamples
            : analysisSamplesContext.singleEndSamples
        }
        style={{ maxHeight: sampleDisplayHeight, overflowY: "auto" }}
        renderItem={item => {
          return (
            <List.Item>
              <List.Item.Meta
                key={item.sampleId}
                className="t-single-end"
                avatar={<Avatar icon={<IconExperiment />} />}
                title={
                  <a
                    href={`${SAMPLES_BASE_URL}/${item.sampleId}/details`}
                    target="_blank"
                    className="t-single-end-sample-name"
                  >
                    {item.sampleName}
                  </a>
                }
                description={
                  <div>
                    <div key={`file-${item.fileId}`}>
                      <a
                        href={`${SEQ_FILES_BASE_URL}/${item.fileId}/file/${item.sequenceFile.identifier}/summary`}
                        target="_blank"
                      >
                        {item.sequenceFile.fileName}
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
      setSingleEndFilteredSamples(analysisSamplesContext.singleEndSamples);
    } else {
      searchStr = String(searchStr).toLowerCase();
      const samplesContainingSearchValue = analysisSamplesContext.samples.filter(
        sample =>
          sample.sampleName.toLowerCase().includes(searchStr) ||
          sample.forward.fileName.toLowerCase().includes(searchStr) ||
          sample.reverse.fileName.toLowerCase().includes(searchStr)
      );

      const singleEndSamplesContainingSearchValue = analysisSamplesContext.singleEndSamples.filter(
        sample =>
          sample.sampleName.toLowerCase().includes(searchStr) ||
          sample.forward.fileName.toLowerCase().includes(searchStr) ||
          sample.reverse.fileName.toLowerCase().includes(searchStr)
      );

      setFilteredSamples(samplesContainingSearchValue);
      setSingleEndFilteredSamples(singleEndSamplesContainingSearchValue);
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
      ) : analysisSamplesContext.samples.length > 0 ||
        analysisSamplesContext.singleEndSamples.length > 0 ? (
        <div>
          <Search
            placeholder={i18n("AnalysisSamples.searchSamples")}
            onChange={event => searchSamples(event.target.value)}
            style={{ width: "100%", marginBottom: SPACE_MD }}
            allowClear={true}
            id="t-sample-search-input"
          />
          {analysisSamplesContext.samples.length > 0
            ? renderPairedEndSamples()
            : null}
          {analysisSamplesContext.singleEndSamples.length > 0
            ? renderSingleEndSamples()
            : null}
        </div>
      ) : (
        <InfoAlert message={i18n("AnalysisSamples.samplesDeleted")} />
      )}
    </>
  );
}

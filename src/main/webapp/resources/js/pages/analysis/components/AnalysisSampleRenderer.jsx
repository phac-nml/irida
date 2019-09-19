/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext, useState } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisSamplesContext } from "../../../contexts/AnalysisSamplesContext";
import { Avatar, Icon, List, Input } from "antd";
import { SPACE_MD } from "../../../styles/spacing";

const { Search } = Input;

export function AnalysisSampleRenderer() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisSamplesContext, sampleDisplayHeight } = useContext(
    AnalysisSamplesContext
  );
  const [filteredSamples, setFilteredSamples] = useState(
    analysisSamplesContext.samples
  );

  const renderSamples = () => {
    const samplesList = [];

    if (analysisSamplesContext.samples.length > 0) {
      return (
        <List
          bordered
          dataSource={filteredSamples}
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
    } else {
      samplesList.push(
        <p key={`no-paired-end-0`}>{getI18N("AnalysisSamples.noPairedEnd")}</p>
      );
    }
    return samplesList;
  };

  /*
   * if search value is empty display all the samples otherwise
   * find samples with sample name or files that contain the search string
   */
  const searchSamples = searchStr => {
    if (searchStr === "") {
      setFilteredSamples(analysisSamplesContext.samples);
    } else {
      const samplesContainingSearchValue = [];

      for (const [index, sample] of analysisSamplesContext.samples.entries()) {
        if (
          sample.sampleName.includes(searchStr) ||
          sample.forward.fileName.includes(searchStr) ||
          sample.reverse.fileName.includes(searchStr)
        ) {
          samplesContainingSearchValue.push(sample);
        }
      }
      setFilteredSamples(samplesContainingSearchValue);
    }
  };

  return (
    <>
      <div>
        <Search
          placeholder={getI18N("AnalysisSamples.inputSearchText")}
          onChange={event => searchSamples(event.target.value)}
          style={{ width: "100%", marginBottom: SPACE_MD }}
        />
        <div style={{ height: sampleDisplayHeight, overflowY: "auto" }}>
          {renderSamples()}
        </div>
      </div>
    </>
  );
}

/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext, useState } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisSamplesContext } from "../../../contexts/AnalysisSamplesContext";
import { Avatar, Icon, List, Input, Typography } from "antd";
import { SPACE_MD } from "../../../styles/spacing";

const { Search } = Input;
const { Text } = Typography;

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
    return (
      <List
        bordered
        dataSource={filteredSamples}
        style={{ height: sampleDisplayHeight, overflowY: "auto" }}
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
    if (searchStr === "") {
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
      {analysisSamplesContext.samples.length > 0 ? (
        <div>
          <Search
            placeholder={getI18N("AnalysisSamples.searchSamples")}
            onChange={event => searchSamples(event.target.value)}
            style={{ width: "100%", marginBottom: SPACE_MD }}
            allowClear={true}
          />
          {renderSamples()}
        </div>
      ) : (
        <Text strong key={`no-paired-end-0`}>
          {getI18N("AnalysisSamples.noPairedEnd")}
        </Text>
      )}
    </>
  );
}

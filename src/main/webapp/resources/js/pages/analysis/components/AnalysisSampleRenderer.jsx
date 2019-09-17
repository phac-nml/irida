/*
 * The following import statements makes available all the elements
 * required by the component
 */

import React, { useContext } from "react";
import { getI18N } from "../../../utilities/i18n-utilties";
import { AnalysisSamplesContext } from "../../../contexts/AnalysisSamplesContext";
import { Avatar, Icon, List } from "antd";

export function AnalysisSampleRenderer() {
  /*
   * The following const statement
   * make the required context which contains
   * the state and methods available to the component
   */
  const { analysisSamplesContext } = useContext(AnalysisSamplesContext);

  const renderSamples = () => {
    const samplesList = [];

    if (analysisSamplesContext.samples.length > 0) {
      return (
        <List
          bordered
          dataSource={analysisSamplesContext.samples}
          renderItem={item => {
            console.log(item);
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

  return <>{renderSamples()}</>;
}

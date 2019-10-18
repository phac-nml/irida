/*
 * This file renders the error details for the analysis
 */

/*
 * The following import statements makes available all the elements
 * required by the component encompassed within
 */

import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { Alert, Button, Col, List, Spin, Tabs, Typography } from "antd";
import { getJobErrors } from "../../../apis/analysis/analysis";
import { formatDate } from "../../../utilities/date-utilities";
import { getI18N } from "../../../utilities/i18n-utilties";
import styled from "styled-components";

const { Paragraph, Text, Title } = Typography;
const TabPane = Tabs.TabPane;

export default function AnalysisError(props) {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);
  const [ currActiveKey, setCurrActiveKey ] = useState(1);

  const StyledTabs = styled(Tabs)`
    .ant-tabs-tab {
      @media only screen and (min-width: 800px) {
        width: 200px;
      }
    }
  `;

  // Sets the job errors into a local state variable on page load
  useEffect(() => {
    getJobErrors(analysisContext.analysis.identifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  // Returns the galaxy job details for the given index
  function galaxyJobDetails(index) {
    return([
      {
        title: getI18N("AnalysisError.createdDate"),
        desc: (
          <Text>
            {formatDate({
              date:
                jobErrors.galaxyJobErrors[index]
                  .createdDate
            })}
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.updatedDate"),
        desc: (
          <Text>
            {formatDate({
              date:
                jobErrors.galaxyJobErrors[index]
                  .updatedDate
            })}
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.commandLine"),
        desc: (
            <Text>
              {jobErrors.galaxyJobErrors[index].commandLine.trim()}
            </Text>
        )
      },
      {
        title: getI18N("AnalysisError.exitCode"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .exitCode
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.toolId"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .toolId
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.toolName"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .toolName
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.toolVersion"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .toolVersion
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.toolDescription"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .toolDescription
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.provenanceId"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .provenanceId
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.provenanceUUID"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .provenanceUUID
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.historyId"),
        desc: (
          <Button
            type="link"
            style={{ paddingLeft: 0 }}
            href={`${jobErrors["galaxyUrl"]}/histories/view?id=${
              jobErrors.galaxyJobErrors[index]
                .historyId
            }`}
            target="_blank"
          >
            {
              jobErrors.galaxyJobErrors[index]
                .historyId
            }
          </Button>
        )
      },
      {
        title: getI18N("AnalysisError.jobId"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .jobId
            }
          </Text>
        )
      },
      {
        title: getI18N("AnalysisError.identifier"),
        desc: (
          <Text>
            {
              jobErrors.galaxyJobErrors[index]
                .identifier
            }
          </Text>
        )
      }
    ]);
  }

  /* Returns a list of the galaxy job information for the provided index
   * which is pulled from the galaxyJobDetails function above
   */
  function getGalaxyJobInfo(index=0) {
    return (
      <List
        itemLayout="horizontal"
        dataSource={galaxyJobDetails(index)}
        renderItem={item => (
          <List.Item>
            <List.Item.Meta
              title={<Text strong>{item.title}</Text>}
              description={item.desc}
            />
          </List.Item>
        )}
      />
    );
  }

  // Returns the standard error for the given index from the jobErrors object
  function getStandardError(index=0) {
    return (
      <pre style={{ whiteSpace: "pre-wrap" }}>
        <Text>
          {jobErrors.galaxyJobErrors[index].standardError.trim()}
        </Text>
      </pre>
    );
  }

  // Returns the standard output for the given index from the jobErrors object
  function getStandardOutput(index=0) {
    return (
      <pre style={{ whiteSpace: "pre-wrap" }}>
        <Text>
          {jobErrors.galaxyJobErrors[index].standardOutput.trim()}
        </Text>
      </pre>
    );
  }

  // Returns the galaxy parameters for the given index from the jobErrors object
  function getGalaxyParameters (index=0) {
    return (
        <div>
            <pre>
                <Text>
                    {JSON.stringify(JSON.parse(jobErrors.galaxyJobErrors[index].parameters.trim()), null, 3) }
                </Text>
            </pre>
        </div>
    );
  }

  // Sets the current activekey for the 'Pass' tabs
  function updateActiveKey(key) {
    setCurrActiveKey(key.charAt(key.length-1));
  }

  /* This function will display 'Pass' tabs within the tabpanes if there is
   * more than one error is returned for an analysis.
   */
  function showPassTabs(tabName) {
    // Since we only need the jobError indexes to generate the tabs we iterate '
    // over the keys instead of the whole galaxyJobErrors object
    return(<Tabs animated={false} onChange={(key) => updateActiveKey(key)} activeKey={`${tabName}-pass-${currActiveKey}`}>
        {Object.keys(jobErrors.galaxyJobErrors).map(key => {
            const index = parseInt(key);
            if(tabName === "galaxy-parameters") {
                return <TabPane tab={`Pass ${index+1}`} key={`${tabName}-pass-${index+1}`}>{getGalaxyParameters(index)}</TabPane>
            }
            else if(tabName === "job-error-info") {
                return <TabPane tab={`Pass ${index+1}`} key={`${tabName}-pass-${index+1}`}>{getGalaxyJobInfo(index)}</TabPane>
            }
            else if(tabName === "standard-error") {
                return <TabPane tab={`Pass ${index+1}`} key={`${tabName}-pass-${index+1}`}>{getStandardError(index)}</TabPane>
            }
            else if(tabName === "standard-out") {
                return <TabPane tab={`Pass ${index+1}`} key={`${tabName}-pass-${index+1}`}>{getStandardOutput(index)}</TabPane>
            }
        })}
    </Tabs>);
  }

  return (
    <>
      {jobErrors !== null ? (
        jobErrors.galaxyJobErrors !== null ? (
          <StyledTabs
            type="card"
            activeKey={
              props.defaultTabKey === "" || props.defaultTabKey === "job-error"
                ? "job-error-info"
                : props.defaultTabKey
            }
            onChange={props.updateNav}
            tabPosition="left"
            animated={false}
          >
            <TabPane
              tab={getI18N("AnalysisError.galaxyJobInfo")}
              key="job-error-info"
            >
              <Col span={12}>
                <Title level={3}>
                  {getI18N("AnalysisError.galaxyJobInfo")}
                </Title>
                {jobErrors.galaxyJobErrors.length > 1 ? showPassTabs("job-error-info") : getGalaxyJobInfo()}
              </Col>
            </TabPane>

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .parameters ? (
              <TabPane
                tab={getI18N("AnalysisError.galaxyParameters")}
                key="galaxy-parameters"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.galaxyParameters")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ? showPassTabs("galaxy-parameters") : getGalaxyParameters()}
                </Col>
              </TabPane>
            ) : null}

            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardError ? (
              <TabPane
                tab={getI18N("AnalysisError.standardError")}
                key="standard-error"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.standardError")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ? showPassTabs("standard-error") : getStandardError()}
                </Col>
              </TabPane>
            ) : null}
            {jobErrors.galaxyJobErrors[jobErrors.galaxyJobErrors.length - 1]
              .standardOutput ? (
              <TabPane
                tab={getI18N("AnalysisError.standardOutput")}
                key="standard-out"
              >
                <Col span={12}>
                  <Title level={3}>{getI18N("AnalysisError.standardOutput")}</Title>
                  {jobErrors.galaxyJobErrors.length > 1 ? showPassTabs("standard-out") : getStandardOutput()}
                </Col>
              </TabPane>
            ) : null}
          </StyledTabs>
        ) : (
          <div style={{ display: "flex" }}>
            <Alert
              type="warning"
              showIcon
              message={
                <Text strong>
                  {getI18N("AnalysisError.noJobInfoAvailable")}
                </Text>
              }
            />
          </div>
        )
      ) : (
        <Spin />
      )}
    </>
  );
}

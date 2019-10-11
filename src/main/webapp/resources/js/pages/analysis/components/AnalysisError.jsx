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

  const galaxyJobDetails =
    jobErrors !== null && "jobErrors" in jobErrors
      ? [
          {
            title: getI18N("AnalysisError.createdDate"),
            desc: (
              <Text>
                {formatDate({
                  date:
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .updatedDate
                })}
              </Text>
            )
          },
          {
            title: getI18N("AnalysisError.commandLine"),
            desc: (
              <div>
                <Text>
                  {jobErrors["jobErrors"][
                    jobErrors["jobErrors"].length - 1
                  ].commandLine.trim()}
                </Text>
              </div>
            )
          },
          {
            title: getI18N("AnalysisError.exitCode"),
            desc: (
              <Text>
                {
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .historyId
                }`}
                target="_blank"
              >
                {
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
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
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .identifier
                }
              </Text>
            )
          }
        ]
      : null;

  const getGalaxyJobInfo = () => {
    return (
      <List
        itemLayout="horizontal"
        dataSource={galaxyJobDetails}
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
  };

  const getStandardError = () => {
    return (
      <div>
        <Col span={12}>
          <Title level={3}>{getI18N("AnalysisError.standardError")}</Title>
          <pre style={{ whiteSpace: "pre-wrap" }}>
            <Text>
              {jobErrors["jobErrors"][
                jobErrors["jobErrors"].length - 1
              ].standardError.trim()}
            </Text>
          </pre>
        </Col>
      </div>
    );
  };

  const getStandardOutput = () => {
    return (
      <div>
        <Col span={12}>
          <Title level={3}>{getI18N("AnalysisError.standardOutput")}</Title>
          <pre style={{ whiteSpace: "pre-wrap" }}>
            <Text>
              {jobErrors["jobErrors"][
                jobErrors["jobErrors"].length - 1
              ].standardOutput.trim()}
            </Text>
          </pre>
        </Col>
      </div>
    );
  };

  const getGalaxyParameters = () => {
    return (
      <div>
        <Col span={12}>
          <Title level={3}>{getI18N("AnalysisError.galaxyParameters")}</Title>
          <pre style={{ whiteSpace: "pre-wrap" }}>
            <Text>
              {jobErrors["jobErrors"][
                jobErrors["jobErrors"].length - 1
              ].parameters.trim()}
            </Text>
          </pre>
        </Col>
      </div>
    );
  };

  return (
    <>
      {jobErrors !== null ? (
        "jobErrors" in jobErrors ? (
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
                {getGalaxyJobInfo()}
              </Col>
            </TabPane>

            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .parameters ? (
              <TabPane
                tab={getI18N("AnalysisError.galaxyParameters")}
                key="galaxy-parameters"
              >
                {getGalaxyParameters()}
              </TabPane>
            ) : null}

            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .standardError ? (
              <TabPane
                tab={getI18N("AnalysisError.standardError")}
                key="standard-error"
              >
                {getStandardError()}
              </TabPane>
            ) : null}
            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .standardOutput ? (
              <TabPane
                tab={getI18N("AnalysisError.standardOutput")}
                key="standard-out"
              >
                {getStandardOutput()}
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

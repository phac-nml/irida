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

export function AnalysisError() {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState(null);

  const Wrapper = styled.div`
    margin-left: 50px;
    padding-top: 25px;
  `;

  // Sets the job errors into a local state variable on page load
  useEffect(() => {
    getJobErrors(analysisContext.analysis.identifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  const galaxyJobDetails = [
    {
      title: getI18N("AnalysisError.createdDate"),
      desc: (
        <Text>
          {jobErrors !== null
            ? formatDate({
                date:
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .createdDate
              })
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.updatedDate"),
      desc: (
        <Text>
          {jobErrors !== null
            ? formatDate({
                date:
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .updatedDate
              })
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.commandLine"),
      desc: (
        <pre style={{ whiteSpace: "pre-wrap" }}>
          <Text>
            {jobErrors !== null
              ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                  .commandLine
              : null}
          </Text>
        </pre>
      )
    },
    {
      title: getI18N("AnalysisError.exitCode"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].exitCode
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.toolId"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].toolId
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.toolName"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].toolName
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.toolVersion"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .toolVersion
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.toolDescription"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .toolDescription
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.provenanceId"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .provenanceId
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.provenanceUUID"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .provenanceUUID
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.historyId"),
      desc: (
        <Button
          type="link"
          style={{ paddingLeft: 0 }}
          href={
            jobErrors !== null
              ? `${jobErrors["galaxyUrl"]}/histories/view?id=${
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .historyId
                }`
              : null
          }
          target="_blank"
        >
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .historyId
            : null}
        </Button>
      )
    },
    {
      title: getI18N("AnalysisError.jobId"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].jobId
            : null}
        </Text>
      )
    },
    {
      title: getI18N("AnalysisError.identifier"),
      desc: (
        <Text>
          {jobErrors !== null
            ? jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .identifier
            : null}
        </Text>
      )
    }
  ];

  const getGalaxyJobInfo = () => {
    return (
      <List
        itemLayout="horizontal"
        dataSource={galaxyJobDetails}
        renderItem={item => (
          <List.Item>
            <List.Item.Meta
              title={<span style={{ fontWeight: "bold" }}>{item.title}</span>}
              description={<span>{item.desc}</span>}
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
              {
                jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                  .standardError
              }
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
              {
                jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                  .standardOutput
              }
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
              {
                jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                  .parameters
              }
            </Text>
          </pre>
        </Col>
      </div>
    );
  };

  return (
    <Wrapper>
      {jobErrors !== null ? (
        "jobErrors" in jobErrors ? (
          <Tabs defaultActiveKey="1" tabPosition="left" animated={false}>
            <TabPane tab={getI18N("AnalysisError.galaxyJobInfo")} key="1">
              <Col span={12}>
                <Title level={3}>
                  {getI18N("AnalysisError.galaxyJobInfo")}
                </Title>
                {getGalaxyJobInfo()}
              </Col>
            </TabPane>

            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .parameters ? (
              <TabPane tab={getI18N("AnalysisError.galaxyParameters")} key="2">
                {getGalaxyParameters()}
              </TabPane>
            ) : null}

            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .standardError ? (
              <TabPane tab={getI18N("AnalysisError.standardError")} key="3">
                {getStandardError()}
              </TabPane>
            ) : null}
            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
              .standardOutput ? (
              <TabPane tab={getI18N("AnalysisError.standardOutput")} key="4">
                {getStandardOutput()}
              </TabPane>
            ) : null}
          </Tabs>
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
    </Wrapper>
  );
}

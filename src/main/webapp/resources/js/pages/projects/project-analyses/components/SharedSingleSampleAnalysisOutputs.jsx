import React from "react";
import { Button, Input, Table, Typography } from "antd";

import { SPACE_MD, SPACE_XS } from "../../../../styles/spacing";
import { IconDownloadFile } from "../../../../components/icons/Icons";

import { useGetSharedSingleSampleAnalysisOutputsQuery } from "../../../../apis/projects/analyses";
import { dateColumnFormat } from "../../../../components/ant.design/table-renderers";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const { Title } = Typography;

export default function SharedSingleSampleAnalysisOutputs({ projectId }) {
  const {
    data: sharedSingleSampleAnalysisOutputs = {},
    isLoading,
  } = useGetSharedSingleSampleAnalysisOutputsQuery(projectId);

  console.log(sharedSingleSampleAnalysisOutputs);

  const [selected, setSelected] = React.useState([]);

  const columns = [
    {
      title: "ID",
      key: "analysisOutputFileId",
      dataIndex: "analysisOutputFileId",
    },
    {
      title: "Sample Name",
      key: "sampleName",
      dataIndex: "sampleName",
      render(sampleName, record) {
        return (
          <a
            href={setBaseUrl(
              `/projects/${projectId}/samples/${record["sampleId"]}`
            )}
          >
            {sampleName}
          </a>
        );
      },
    },
    {
      title: "File",
      key: "fileName",
      dataIndex: "filePath",
      render(filePath, record) {
        let splitItems = filePath.split("/");
        let fileName = splitItems[splitItems.length - 1];
        return fileName + " (" + record["analysisOutputFileKey"] + ")";
      },
    },
    {
      title: "Analysis Type",
      key: "analysisType",
      dataIndex: "analysisType",
      render({ type }) {
        return type;
      },
    },
    {
      title: "Pipeline",
      key: "pipelineName",
      dataIndex: "workflowId",
    },
    {
      title: "Analysis Submission",
      key: "analysisSubmissionName",
      dataIndex: "analysisSubmissionName",
      render(analysisSubmissionName, record) {
        return (
          <a href={setBaseUrl(`/analysis/${record["analysisSubmissionId"]}`)}>
            {analysisSubmissionName}
          </a>
        );
      },
    },
    {
      title: "Submitter",
      key: "submitter",
      dataIndex: "userFirstName",
      render(userFirstName, record) {
        return userFirstName + " " + record["userLastName"];
      },
    },
    {
      ...dateColumnFormat(),
      title: "Date Created",
      key: "createdDate",
      dataIndex: "createdDate",
    },
  ];

  let rowSelection;
  rowSelection = {
    selectedRowKeys: selected,
    onChange: (selectedRowKeys) => setSelected(selectedRowKeys),
    getCheckboxProps: (record) => ({
      name: record.analysisOutputFileId,
    }),
  };

  return (
    <>
      <Title level={2}>Shared Single Sample Analysis Outputs</Title>
      <div
        style={{
          paddingBottom: SPACE_MD,
          display: "flex",
          justifyContent: "space-between",
        }}
      >
        <Button>
          <IconDownloadFile style={{ marginRight: SPACE_XS }} />
          Download
        </Button>
        <Input.Search style={{ width: 300 }} />
      </div>
      <Table
        loading={isLoading}
        scroll={{ x: "max-content" }}
        columns={columns}
        dataSource={!isLoading && sharedSingleSampleAnalysisOutputs}
        tableLayout="auto"
        rowSelection={rowSelection}
      />
    </>
  );
}

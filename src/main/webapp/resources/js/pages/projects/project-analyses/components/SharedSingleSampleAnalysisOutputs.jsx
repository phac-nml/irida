import React from "react";
import { Button, Input, Table, Typography } from "antd";

import { PagedTableProvider } from "../../../../components/ant.design/PagedTable";
import { SPACE_MD, SPACE_XS } from "../../../../styles/spacing";
import { IconDownloadFile } from "../../../../components/icons/Icons";

const { Title } = Typography;

export default function SharedSingleSampleAnalysisOutputs() {
  const columns = [
    {
      title: "Sample Name",
    },
    {
      title: "File",
    },
    {
      title: "Analysis Type",
    },
    {
      title: "Pipeline",
    },
    {
      title: "Analysis Submission",
    },
    {
      title: "Submitter",
    },
    {
      title: "Date Created",
    },
  ];

  return (
    <PagedTableProvider url="">
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
        scroll={{ x: "max-content" }}
        columns={columns}
        tableLayout="auto"
      />
    </PagedTableProvider>
  );
}

import React from "react";
import SamplesTable from "../../components/project/samples-table/SamplesTable";
import { ProjectSamplesProvider } from "../../components/project/samples-table/useProjectSamplesContext";
import { Row, Space } from "antd";
import SampleTools from "../../components/project/samples-table/components/SampleTools";

/**
 * React component to render the layout for the project > samples pages
 * @constructor
 */
export default function ProjectSamplesLayout(): JSX.Element {
  return (
    <ProjectSamplesProvider>
      <Row gutter={[16, 16]}>
        <Space direction={"horizontal"}>
          <SampleTools />
        </Space>
        <SamplesTable />
      </Row>
    </ProjectSamplesProvider>
  );
}

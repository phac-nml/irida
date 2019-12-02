import React from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";

const { Content } = Layout;

export default function AnalysisOutputFiles() {
  const BASE_URL = `${window.PAGE.base}`;
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: "white" }}>
      <Content>
        <OutputFilePreviewContainer path={`${BASE_URL}/file_preview`} />
      </Content>
    </Layout>
  );
}

import React, { useEffect } from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import {} from "../../../apis/analysis/analysis";

const { Content } = Layout;

export default function AnalysisProvenance() {
  useEffect(() => {}, []);
  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <Content></Content>
    </Layout>
  );
}

import React from "react";
import { Layout } from "antd";
import { SPACE_MD } from "../../../styles/spacing";
import { grey1 } from "../../../styles/colors";
import { TabPaneContent } from "../../../components/tabs/TabPaneContent";


export default function FastQCImages() {

  return (
    <Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>
      <TabPaneContent title={`FastQC Images`}>
        <p>On images page</p>
      </TabPaneContent>
    </Layout>
  );
}
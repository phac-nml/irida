import { Alert, Form, PageHeader, Space, Tabs } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import store from "./store";

/**
IGNORE THIS WILL BE MOVED / ENSURING ROUTER WORKING
 */
const ShareMetadata = () => <div>METAsDATA</div>;

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const { originalSamples } = useSelector((state) => state.shareReducer);

  return (
    <PageHeader
      ghost={false}
      title={i18n("ShareSamples.title")}
      subTitle={`Samples selected on project samples page`}
    >
      {originalSamples.length > 0 ? (
        <Form layout="vertical">
          <Space direction="vertical" style={{ display: "block" }} size="large">
            <ShareProject />

            <Tabs defaultActiveKey="samples">
              <Tabs.TabPane tab={"Samples"} key="samples">
                <ShareSamples />
              </Tabs.TabPane>
              <Tabs.TabPane tab={"Metadata Security"} key="metadata">
                <ShareMetadata />
              </Tabs.TabPane>
            </Tabs>
          </Space>
        </Form>
      ) : (
        <Alert
          showIcon
          type="info"
          message={`You have no samples selected to share, please go back to the project samples page and select some samples`}
        />
      )}
    </PageHeader>
  );
}

render(
  <Provider store={store}>
    <ShareLayout />
  </Provider>,
  document.querySelector("#root")
);

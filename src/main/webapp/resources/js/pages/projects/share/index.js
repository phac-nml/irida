import { Alert, PageHeader, Space } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const { originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  return (
    <PageHeader
      ghost={false}
      title={i18n("ShareSamples.title")}
      subTitle={`Samples selected on project samples page`}
      onBack={() =>
        (window.location.href = setBaseUrl(
          `/projects/${currentProject}/samples`
        ))
      }
    >
      {originalSamples.length > 0 ? (
        <Space direction="vertical" style={{ display: "block" }} size="large">
          <ShareProject />
          <ShareSamples />
        </Space>
      ) : (
        <Alert showIcon type="info" message={i18n("ShareSamples.no-samples")} />
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

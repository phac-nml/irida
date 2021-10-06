import { Col, PageHeader, Row } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareLayout } from "./ShareLayout";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareApp() {
  const { originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  /**
   * Return to previous page (project samples page)
   */
  const goToPrevious = () =>
    (window.location.href = setBaseUrl(`/projects/${currentProject}/samples`));

  return (
    <Row>
      <Col xl={{ span: 12, offset: 6 }} lg={{ span: 18, offset: 3 }} xs={24}>
        <PageHeader
          ghost={false}
          title={i18n("ShareSamples.title")}
          onBack={goToPrevious}
        >
          <ShareLayout />
        </PageHeader>
      </Col>
    </Row>
  );
}

render(
  <Provider store={store}>
    <ShareApp />
  </Provider>,
  document.querySelector("#root")
);

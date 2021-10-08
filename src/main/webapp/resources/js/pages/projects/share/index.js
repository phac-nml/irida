import { Col, PageHeader, Row } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider, useSelector } from "react-redux";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareLayout } from "./ShareLayout";
import { ShareNoSamples } from "./ShareNoSamples";
import store from "./store";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareApp() {
  /*
  Create redirect href to project samples page.
  */
  const [redirect] = React.useState(
    () => window.location.href.match(/(.*)\/share/)[1]
  );

  const { originalSamples, currentProject } = useSelector(
    (state) => state.shareReducer
  );

  /*
  1. No Samples - this would be if the user came to this page from anything
  other than the share samples link.
   */
  const NO_SAMPLES =
    typeof originalSamples === "undefined" || originalSamples.length === 0;

  if (NO_SAMPLES) {
    return <ShareNoSamples redirect={redirect} />;
  }

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

import React from "react";
import { useLaunch } from "./launch-context";
import { LaunchForm } from "./LaunchForm";
import { Alert, Typography } from "antd";
import { CART } from "../../utilities/events-utilities";
import { PageWrapper } from "../../components/page/PageWrapper";
import { setBaseUrl } from "../../utilities/url-utilities";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [{ pipeline }] = useLaunch();

  const [validCart, setValidCart] = React.useState(true);

  /**
   * Listen for an empty cart, if empty, show warning that the pipeline cannot be run
   */
  React.useEffect(() => {
    function handleEmptyCart(e) {
      setValidCart(e.detail.count > 0);
    }

    window.addEventListener(CART.UPDATED, handleEmptyCart);
    return () => window.removeEventListener(CART.UPDATED, handleEmptyCart);
  }, []);

  return (
    <>
      {validCart ? (
        <PageWrapper
          title={pipeline.name}
          onBack={() => (window.location.href = setBaseUrl(`cart/pipelines`))}
        >
          <>
            <Typography.Paragraph type="secondary">
              {pipeline.description}
            </Typography.Paragraph>
            <LaunchForm />
          </>
        </PageWrapper>
      ) : (
        <Alert
          type="error"
          showIcon
          message={i18n("LaunchContent.noSamples.message")}
          description={i18n("LaunchContent.noSamples.description")}
        />
      )}
    </>
  );
}

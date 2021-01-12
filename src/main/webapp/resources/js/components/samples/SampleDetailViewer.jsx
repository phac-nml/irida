import React from "react";
import { Button, Modal, Skeleton, Typography } from "antd";
import { fetchSampleDetails } from "../../apis/samples/samples";
import { SampleDetails } from "./components/SampleDetails";

const { Text } = Typography;

/**
 * React component to render details (metadata and files) for a sample.
 * @param sampleId - identifier for a sample
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetailViewer({
  sampleId,
  removeSample = Function.prototype,
  children,
}) {
  const [loading, setLoading] = React.useState(true);
  const [details, setDetails] = React.useState({});
  const [visible, setVisible] = React.useState(false);

  React.useEffect(() => {
    if (visible) {
      fetchSampleDetails(sampleId)
        .then(setDetails)
        .then(() => setLoading(false));
    }
  }, [visible]);

  const removeSampleFromCart = () => {
    removeSample({ projectId: details.projectId, sampleId });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible ? (
        <Modal
          className="t-sample-details-modal"
          bodyStyle={{
            padding: 0,
            maxHeight: window.innerHeight - 400,
            overflowY: "auto",
          }}
          title={
            loading ? null : (
              <div
                style={{
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                }}
              >
                <Text strong>
                  <span className="t-sample-details-name">
                    {details.sample.sampleName}
                  </span>
                </Text>
                <Button
                  size="small"
                  danger
                  style={{ marginRight: 30 }}
                  onClick={removeSampleFromCart}
                >
                  {i18n("SampleDetailsSidebar.removeFromCart")}
                </Button>
              </div>
            )
          }
          visible={visible}
          onCancel={() => setVisible(false)}
          footer={null}
          width={720}
        >
          <div style={{ margin: 24 }}>
            {loading ? (
              <Skeleton active title />
            ) : (
              <SampleDetails details={details} />
            )}
          </div>
        </Modal>
      ) : null}
    </>
  );
}

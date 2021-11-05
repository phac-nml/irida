import { Button, Modal, Skeleton, Typography } from "antd";
import React from "react";
import { SampleDetails } from "./components/SampleDetails";
import { Provider } from "react-redux";
import store from "../../components/samples/store";
import { useGetSampleDetailsQuery } from "../../apis/samples/samples";

const { Text } = Typography;

/**
 * Function to render (details, metadata, files, and analyses) for a sample.
 * @param sampleId - identifier for a sample
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
function DisplaySampleDetails({ sampleId, removeSample, children }) {
  const [visible, setVisible] = React.useState(false);
  const { data: details = {}, isLoading } = useGetSampleDetailsQuery(sampleId, {
    skip: !visible,
  });

  /*
  Empty useEffect hook to update visible const required by redux
  call "useGetSampleDetailsQuery" above
   */
  React.useEffect(() => {}, [visible]);

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
            isLoading ? null : (
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
                {removeSample && (
                  <Button
                    size="small"
                    danger
                    style={{ marginRight: 30 }}
                    onClick={removeSampleFromCart}
                  >
                    {i18n("SampleDetailsSidebar.removeFromCart")}
                  </Button>
                )}
              </div>
            )
          }
          visible={visible}
          onCancel={() => setVisible(false)}
          footer={null}
          width={720}
        >
          <div style={{ margin: 24 }}>
            {isLoading ? (
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

/**
 * React component to provide redux store to sampledetailviewer
 * @param sampleId - identifier for a sample
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetailViewer({ sampleId, removeSample, children }) {
  return (
    <Provider store={store}>
      <DisplaySampleDetails sampleId={sampleId} removeSample={removeSample}>
        {children}
      </DisplaySampleDetails>
    </Provider>
  );
}

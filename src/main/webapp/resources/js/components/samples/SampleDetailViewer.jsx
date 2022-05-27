import { Button, Modal, Skeleton, Space, Tag, Typography } from "antd";
import React from "react";
import { SampleDetails } from "./components/SampleDetails";
import { Provider } from "react-redux";
import store from "../../components/samples/store";
import { useGetSampleDetailsQuery } from "../../apis/samples/samples";
import {
  putSampleInCart,
  useRemoveSampleMutation,
  useGetCartSampleIdsQuery,
} from "../../apis/cart/cart";

const { Text } = Typography;

/**
 * Function to render (details, metadata, files, and analyses) for a sample.
 * @param sampleId - identifier for a sample
 * @param projectId - identifier for a project
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
function DisplaySampleDetails({ sampleId, projectId, children }) {
  const [visible, setVisible] = React.useState(false);
  const [addSampleToCartVisible, setAddSampleToCartVisible] =
    React.useState(true);
  const [removeSampleFromCartVisible, setRemoveSampleFromCartVisible] =
    React.useState(false);
  const { data: details = {}, isLoading } = useGetSampleDetailsQuery(
    {
      sampleId,
      projectId,
    },
    {
      skip: !visible,
    }
  );
  const { data: sampleIds = {} } = useGetCartSampleIdsQuery({ skip: !visible });
  const [removeSample] = useRemoveSampleMutation();

  const isSampleAlreadyInCart = () => {
    return sampleIds.includes(sampleId);
  };

  /*
  Empty useEffect hook to update visible const required by redux
  call "useGetSampleDetailsQuery" above
   */
  React.useEffect(() => {}, [visible]);

  const removeSampleFromCart = () => {
    removeSample({ sampleId }).then((res) => {
      setAddSampleToCartVisible(true);
      setRemoveSampleFromCartVisible(false);
    });
  };

  const addSampleToCart = () => {
    putSampleInCart(projectId, [details.sample]).then((res) => {
      setAddSampleToCartVisible(false);
      setRemoveSampleFromCartVisible(true);
    });
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
                <Space direction="horiztontal" size="small">
                  <Text className="t-sample-details-name" strong>
                    {details.sample.sampleName}
                  </Text>
                  {projectId ? (
                    <Text className="t-sample-details-project-name" strong>
                      <Tag color="#87d068">{details.projectName}</Tag>
                    </Text>
                  ) : null}
                </Space>
                {!isSampleAlreadyInCart() ? (
                  <Button
                    size="small"
                    style={{ marginRight: 30 }}
                    onClick={addSampleToCart}
                  >
                    Add To Cart
                  </Button>
                ) : null}
                {isSampleAlreadyInCart() ? (
                  <Button
                    size="small"
                    danger
                    style={{ marginRight: 30 }}
                    onClick={removeSampleFromCart}
                  >
                    {i18n("SampleDetailsSidebar.removeFromCart")}
                  </Button>
                ) : null}
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
 * @param projectId - identifier for a project
 * @param removeSample - function to remove the sample from the cart.
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleDetailViewer({
  sampleId,
  projectId,
  removeSample,
  children,
}) {
  return (
    <Provider store={store}>
      <DisplaySampleDetails
        sampleId={sampleId}
        projectId={projectId}
        removeSample={removeSample}
      >
        {children}
      </DisplaySampleDetails>
    </Provider>
  );
}

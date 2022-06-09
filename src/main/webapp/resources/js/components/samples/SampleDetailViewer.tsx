import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { Button, Modal, Skeleton, Space, Tag, Typography } from "antd";
import { SampleDetails } from "./components/SampleDetails";
import { useGetSampleDetailsQuery } from "../../apis/samples/samples";
import {
  putSampleInCart,
  getCartSampleIds,
  removeSample,
} from "../../apis/cart/cart";
import {
  setCartSampleIds,
  addCartSampleId,
  removeCartSampleId,
} from "./cartSamplesSlice";
import { Provider } from "react-redux";
import store from "../../components/samples/store";
import { generateColourForItem } from "../../utilities/colour-utilities";
import { Project, Sample } from "../../types/irida";

const { Text } = Typography;

export interface DisplaySampleDetailsProps {
  sampleId: Pick<Sample, "id">;
  projectId: Pick<Project, "id">;
  displayActions?: boolean;
  children: React.ReactNode;
}

/**
 * Function to render (details, metadata, files, and analyses) for a sample.
 * @param sampleId - identifier for a sample
 * @param projectId - identifier for a project
 * @param displayActions - Whether to display add to/remove from cart buttons. Displayed by default
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
function DisplaySampleDetails({
  sampleId,
  projectId,
  displayActions,
  children,
}: DisplaySampleDetailsProps): JSX.Element {
  const dispatch = useDispatch();

  const [visible, setVisible] = React.useState(false);
  const { data: details = {}, isLoading } = useGetSampleDetailsQuery(
    {
      sampleId,
      projectId,
    },
    {
      skip: !visible,
    }
  );
  const { sampleIds } = useSelector((state) => state.cartSamplesReducer);
  const projectColour = generateColourForItem({
    id: projectId,
    label: details.projectName,
  });

  /*
  If sampleIds haven't already been added to
  the state then we go ahead and do that on
  first render
   */
  React.useEffect(() => {
    if (sampleIds.length === 0) {
      getCartSampleIds().then((res) => {
        dispatch(setCartSampleIds({ sampleIds: res }));
      });
    }
  }, [sampleIds.length]);

  /*
  Check if the current sample is in the cart or not
  Used to display `add to cart` and `remove from cart` buttons
   */
  const isSampleAlreadyInCart = () => {
    return sampleIds.includes(parseInt(sampleId));
  };

  /*
  Add sample to cart and update the state sampleIds
   */
  const addSampleToCart = () => {
    putSampleInCart(projectId, [details.sample]).then(() => {
      dispatch(addCartSampleId({ sampleId: details.sample.identifier }));
    });
  };

  /*
  Remove sample from cart and update the state sampleIds
   */
  const removeSampleFromCart = () => {
    removeSample(projectId, sampleId).then(() => {
      dispatch(removeCartSampleId({ sampleId }));
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
                    <Text className="t-sample-details-project-name">
                      <Tag
                        color={projectColour.background}
                        style={{ border: `1px solid ${projectColour.text}` }}
                      >
                        <span style={{ color: projectColour.text }}>
                          {details.projectName}
                        </span>
                      </Tag>
                    </Text>
                  ) : null}
                </Space>
                {displayActions ? (
                  !isSampleAlreadyInCart() ? (
                    <Button
                      size="small"
                      className="t-add-sample-to-cart"
                      style={{ marginRight: 30 }}
                      onClick={addSampleToCart}
                    >
                      {i18n("SampleDetailsViewer.addToCart")}
                    </Button>
                  ) : (
                    <Button
                      size="small"
                      className="t-remove-sample-from-cart"
                      danger
                      style={{ marginRight: 30 }}
                      onClick={removeSampleFromCart}
                    >
                      {i18n("SampleDetailsViewer.removeFromCart")}
                    </Button>
                  )
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
 * @param displayActions - Whether to display add to/remove from cart buttons. Displayed by default
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */

export interface SampleDetailViewerProps {
  sampleId: Pick<Sample, "id">;
  projectId: Pick<Project, "id">;
  displayActions?: boolean;
  children: React.ReactNode;
}

export function SampleDetailViewer({
  sampleId,
  projectId,
  displayActions = true,
  children,
}: SampleDetailViewerProps): JSX.Element {
  return (
    <Provider store={store}>
      <DisplaySampleDetails
        sampleId={sampleId}
        projectId={projectId}
        displayActions={displayActions}
      >
        {children}
      </DisplaySampleDetails>
    </Provider>
  );
}

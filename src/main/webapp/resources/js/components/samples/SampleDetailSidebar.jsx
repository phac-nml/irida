import React from "react";
import { Button, Drawer, List, Skeleton, Typography } from "antd";
import { getSampleDetails } from "../../apis/samples/samples";
import { connect } from "react-redux";
import { actions } from "../../redux/reducers/cart";
import { IconShoppingCart } from "../icons/Icons";

const { Text } = Typography;

function SampleDetail({ sampleId, removeSample, children }) {
  const [loading, setLoading] = React.useState(true);
  const [details, setDetails] = React.useState({});
  const [visible, setVisible] = React.useState(false);

  React.useEffect(() => {
    if (visible) {
      getSampleDetails(sampleId)
        .then(setDetails)
        .then(() => setLoading(false));
    }
  }, [visible]);

  const removeSampleFromCart = () => {
    removeSample({ project: { id: details.projectId }, id: sampleId });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Drawer
        title={
          loading ? null : (
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
            >
              <Text strong>{details.sample.sampleName}</Text>
              <Button
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
        onClose={() => setVisible(false)}
        width={720}
      >
        {loading ? (
          <Skeleton active title />
        ) : (
          <div>
            <List
              itemLayout="horizontal"
              dataSource={Object.keys(details.metadata)}
              renderItem={(item) => (
                <List.Item>
                  <List.Item.Meta
                    title={item}
                    description={details.metadata[item].value}
                  />
                </List.Item>
              )}
            />
          </div>
        )}
      </Drawer>
    </>
  );
}

const mapDispatchToProps = (dispatch) => ({
  removeSample: (details) => dispatch(actions.removeSample(details)),
});

export const SampleDetailSidebar = connect(
  undefined,
  mapDispatchToProps
)(SampleDetail);

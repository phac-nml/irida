import React from "react";
import { Button, Drawer, List, Skeleton } from "antd";
import { getSampleDetails } from "../../apis/samples/samples";
import { removeSample } from "../../apis/cart/cart";

export function SampleDetailSidebar({ sampleId, children }) {
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
    removeSample(undefined, sampleId);
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      <Drawer
        title={details.sample?.sampleName}
        visible={visible}
        onClose={() => setVisible(false)}
        width={720}
      >
        {loading ? (
          <Skeleton active title />
        ) : (
          <div>
            <div style={{ display: "flex", flexDirection: "row-reverse" }}>
              <Button onClick={removeSampleFromCart}>Remove</Button>
            </div>
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

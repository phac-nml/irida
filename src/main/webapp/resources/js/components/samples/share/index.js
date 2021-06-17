import { Avatar, List, Modal } from "antd";
import React from "react";
import { yellow6 } from "../../../styles/colors";
import { IconLocked } from "../../icons/Icons";

export default function CopySamples({ children, getSelectedSamples }) {
  const [visible, setVisible] = React.useState(false);
  const [samples, setSamples] = React.useState([]);

  const getSamples = () => {
    setSamples(getSelectedSamples());
    setVisible(true);
  };

  console.log(samples);

  return (
    <>
      {React.cloneElement(children, {
        onClick: getSamples,
      })}
      <Modal visible={visible} onCancel={() => setVisible(false)} width={`90%`}>
        <List
          itemLayout="horizontal"
          dataSource={samples}
          renderItem={(sample) => (
            <List.Item actions={[<a key="remove">remove</a>]}>
              <List.Item.Meta
                avatar={
                  sample.owner ? null : (
                    <Avatar
                      size={"smalll"}
                      style={{ backgroundColor: yellow6 }}
                      icon={<IconLocked style={{ color: "#222222" }} />}
                    />
                  )
                }
                title={sample.sampleName}
                description={
                  sample.owner
                    ? null
                    : "`This sample will be locked from modification in the new project`"
                }
              />
            </List.Item>
          )}
        />
      </Modal>
    </>
  );
}

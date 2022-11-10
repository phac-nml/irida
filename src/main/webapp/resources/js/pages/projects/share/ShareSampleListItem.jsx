import { Avatar, Button, List, Tooltip } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { IconLocked, IconUnlocked } from "../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { green6 } from "../../../styles/colors";
import { removeSample } from "./shareSlice";
import { UnlockTwoTone } from "@ant-design/icons";

/**
 * Render a list item for the samples to be shared with another project.
 * @param {array} sample - sample to display
 * @param {object} style - style to apply to the list item
 * @returns
 */
export default function ShareSamplesListItem({
  sample,
  style,
  actionsRequired,
}) {
  const dispatch = useDispatch();

  return (
    <List.Item
      style={{ ...style }}
      className="t-share-sample"
      actions={
        actionsRequired
          ? [
              <Button
                key="remove"
                type="link"
                onClick={() => dispatch(removeSample(sample.id))}
              >
                {i18n("ShareSamples.remove")}
              </Button>,
            ]
          : []
      }
    >
      <List.Item.Meta
        avatar={
          <Avatar
            style={{ backgroundColor: `transparent` }}
            className="t-unlocked-sample"
            icon={<UnlockTwoTone twoToneColor={green6} />}
          />
        }
        title={
          <SampleDetailViewer
            sampleId={sample.id}
            projectId={sample.projectId}
            displayActions={false}
          >
            <Button>{sample.name}</Button>
          </SampleDetailViewer>
        }
      />
    </List.Item>
  );
}

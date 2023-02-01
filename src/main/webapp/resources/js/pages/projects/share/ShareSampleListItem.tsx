import { Avatar, Button, List } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { UnlockTwoTone } from "@ant-design/icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { green6 } from "../../../styles/colors";
import type { SelectedSample } from "../types";
import { removeSample } from "./shareSlice";

type ShareSamplesListItemProps = {
  sample: SelectedSample;
  style: React.CSSProperties;
  actionsRequired: boolean;
};

/**
 * Render a list item for the samples to be shared with another project.
 * @param sample - sample to display
 * @param style - style to apply to the list item
 * @param actionsRequired
 * @returns
 */
export default function ShareSamplesListItem({
  sample,
  style,
  actionsRequired,
}: ShareSamplesListItemProps) {
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
            <Button>{sample.sampleName}</Button>
          </SampleDetailViewer>
        }
      />
    </List.Item>
  );
}

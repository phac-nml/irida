import { Avatar, Button, List, Tooltip } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import { FixedSizeList as VList } from "react-window";
import {
  IconLocked,
  IconRemove,
  IconUnlocked,
} from "../../../components/icons/Icons";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { green6, grey1, grey2, yellow6 } from "../../../styles/colors";
import { removeSample } from "./shareSlice";

/**
 * Component to render a virtual list of sample to be copied to another project.
 * @param list
 * @returns {JSX.Element}
 * @constructor
 */
export function SharedSamplesList({ list = [] }) {
  const dispatch = useDispatch();

  const Row = ({ index, style }) => {
    const sample = list[index];

    return (
      <List.Item
        style={{ ...style, backgroundColor: grey1 }}
        actions={[
          <Tooltip
            key="remove"
            placement="left"
            title={i18n("ShareSamples.remove")}
          >
            <Button
              size="small"
              shape="circle"
              icon={<IconRemove />}
              onClick={() => dispatch(removeSample(sample.id))}
            />
          </Tooltip>,
        ]}
      >
        <List.Item.Meta
          avatar={
            sample.owner ? (
              <Tooltip
                title={i18n("ShareSamples.avatar.unlocked")}
                placement="right"
                color={green6}
              >
                <Avatar
                  style={{ backgroundColor: green6 }}
                  size="small"
                  icon={<IconUnlocked />}
                />
              </Tooltip>
            ) : (
              <Tooltip title={i18n("ShareSamples.avatar.locked")}>
                <Avatar
                  style={{ backgroundColor: yellow6 }}
                  size="small"
                  icon={<IconLocked />}
                />
              </Tooltip>
            )
          }
          title={
            <SampleDetailViewer sampleId={sample.id}>
              <Button>{sample.name}</Button>
            </SampleDetailViewer>
          }
        />
      </List.Item>
    );
  };

  const ROW_HEIGHT = 55;
  const MAX_LIST_HEIGHT = 600;
  const height =
    list.length * ROW_HEIGHT < MAX_LIST_HEIGHT
      ? list.length * ROW_HEIGHT + 2 // 2 is to offset for borders
      : MAX_LIST_HEIGHT;
  return (
    <List
      bordered
      rowKey={(sample) => sample.name}
      style={{ backgroundColor: grey2 }}
    >
      <VList
        height={height}
        itemCount={list.length}
        itemSize={ROW_HEIGHT}
        width="100%"
      >
        {Row}
      </VList>
    </List>
  );
}

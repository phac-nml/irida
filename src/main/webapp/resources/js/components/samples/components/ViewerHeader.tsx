import { Button, Menu, Space, Tag, Typography } from "antd";
import React, { Dispatch, SetStateAction, useMemo } from "react";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { generateColourForItem } from "../../../utilities/colour-utilities";
import { ViewerTab } from "../SampleDetailViewer";
import {
  addSampleToCartThunk,
  removeSampleFromCartThunk,
} from "../sampleSlice";

export const HEADER_HEIGHT = 90;

export default function ViewerHeader({
  displayActions,
  projectId,
  sampleId,
  refetch,
  tab,
  onMenuChange,
}: {
  displayActions: boolean;
  projectId: number;
  sampleId: number;
  tab: ViewerTab;
  onMenuChange: Dispatch<SetStateAction<ViewerTab>>;
}): JSX.Element {
  const dispatch = useAppDispatch();
  const { inCart, sample, projectName } = useAppSelector(
    (state) => state.sampleReducer
  );

  const projectColour = useMemo(
    () =>
      generateColourForItem({
        id: projectId,
        label: projectName,
      }),
    [projectId, projectName]
  );

  return (
    <div
      style={{
        marginTop: 5,
        marginBottom: 2,
        borderBottom: `1px solid var(--grey-4)`,
        height: HEADER_HEIGHT,
      }}
    >
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          margin: `10px 50px 0 20px`,
        }}
      >
        <Space>
          <Typography.Title level={3} style={{ marginBottom: 0 }}>
            {sample.label}
          </Typography.Title>
          <Tag
            color={projectColour.background}
            style={{ border: `1px solid ${projectColour.text}` }}
          >
            <span style={{ color: projectColour.text }}>{projectName}</span>
          </Tag>
        </Space>
        {displayActions && inCart && (
          <Button
            size="small"
            className="t-remove-sample-from-cart"
            danger
            onClick={() => dispatch(removeSampleFromCartThunk())}
          >
            {i18n("SampleDetailsViewer.removeFromCart")}
          </Button>
        )}
        {displayActions && !inCart && (
          <Button
            size="small"
            className="t-add-sample-to-cart"
            onClick={() => dispatch(addSampleToCartThunk())}
          >
            {i18n("SampleDetailsViewer.addToCart")}
          </Button>
        )}
      </div>
      <Menu
        mode="horizontal"
        defaultSelectedKeys={[tab]}
        onSelect={({ key }) => onMenuChange(key)}
        style={{ borderBottom: 0 }}
      >
        <Menu.Item key="details">{i18n("SampleDetails.details")}</Menu.Item>
        <Menu.Item key="metadata">{i18n("SampleDetails.metadata")}</Menu.Item>
        <Menu.Item key="files">{i18n("SampleDetails.files")}</Menu.Item>
        <Menu.Item key="analyses">{i18n("SampleDetails.analyses")}</Menu.Item>
      </Menu>
    </div>
  );
}

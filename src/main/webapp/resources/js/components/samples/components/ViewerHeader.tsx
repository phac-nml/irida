import { Button, Menu, Space, Tag, Typography } from "antd";
import React, { Dispatch, SetStateAction, useMemo } from "react";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { generateColourForItem } from "../../../utilities/colour-utilities";
import { ViewerTab } from "./SampleDetailsModal";

import { updateSampleInCart } from "../sampleSlice";
import {
  usePutSampleInCartMutation,
  useRemoveSampleFromCartMutation,
} from "../../../apis/samples/samples";

export const HEADER_HEIGHT = 90;
export const HEADER_HEIGHT_WITH_PADDING = 97;

export default function ViewerHeader({
  displayActions,
  projectId,
  refetchCart,
  tab,
  onMenuChange,
}: {
  displayActions: boolean;
  projectId: number;
  sampleId: number;
  refetchCart: undefined | (() => void);
  tab: ViewerTab;
  onMenuChange: Dispatch<SetStateAction<ViewerTab>>;
}): JSX.Element {
  const dispatch = useAppDispatch();
  const { inCart, sample, projectName } = useAppSelector(
    (state) => state.sampleReducer
  );

  const [removeSampleFromCart] = useRemoveSampleFromCartMutation();
  const [addSampleToCart] = usePutSampleInCartMutation();

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
          <Typography.Title
            level={3}
            style={{ marginBottom: 0 }}
            className="t-sample-details-name"
          >
            {sample.label}
          </Typography.Title>
          <Tag
            color={projectColour.background}
            style={{ border: `1px solid ${projectColour.text}` }}
          >
            <span
              style={{ color: projectColour.text }}
              className="t-sample-details-project-name"
            >
              {projectName}
            </span>
          </Tag>
        </Space>
        {displayActions && inCart && (
          <Button
            size="small"
            className="t-remove-sample-from-cart"
            danger
            onClick={() => {
              removeSampleFromCart({ sampleId: sample.identifier }).then(() => {
                dispatch(updateSampleInCart({ inCart: false }));
                if (typeof refetchCart !== "undefined") refetchCart();
              });
            }}
          >
            {i18n("SampleDetailsViewer.removeFromCart")}
          </Button>
        )}
        {displayActions && !inCart && (
          <Button
            size="small"
            className="t-add-sample-to-cart"
            onClick={() => {
              addSampleToCart({ projectId, samples: [sample] }).then(() => {
                dispatch(updateSampleInCart({ inCart: true }));
                if (typeof refetchCart !== "undefined") refetchCart();
              });
            }}
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
        className="t-sample-viewer-nav"
      >
        <Menu.Item key="details">{i18n("SampleDetails.details")}</Menu.Item>
        <Menu.Item key="metadata">{i18n("SampleDetails.metadata")}</Menu.Item>
        <Menu.Item key="files">{i18n("SampleDetails.files")}</Menu.Item>
        <Menu.Item key="analyses">{i18n("SampleDetails.analyses")}</Menu.Item>
      </Menu>
    </div>
  );
}

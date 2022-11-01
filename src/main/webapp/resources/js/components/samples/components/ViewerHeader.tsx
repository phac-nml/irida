import { Button, MenuProps, Space, Tag, Typography } from "antd";
import React, { Dispatch, SetStateAction, useMemo } from "react";
import { useAppDispatch, useAppSelector } from "../../../hooks/useState";
import { generateColourForItem } from "../../../utilities/colour-utilities";
import { ViewerTab } from "./SampleDetailsModal";

import {
  addSampleToCartThunk,
  removeSampleFromCartThunk,
} from "../sampleSlice";
import HorizontalMenu from "../../ant.design/HorizontalMenu";

export const HEADER_HEIGHT = 90;
export const HEADER_HEIGHT_WITH_PADDING = 97;

export default function ViewerHeader({
  displayActions,
  projectId,
  refetch,
  tab,
  onMenuChange,
}: {
  displayActions: boolean;
  projectId: number;
  sampleId: number;
  refetch: undefined | (() => void);
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

  const menuItems: MenuProps["items"] = useMemo(
    () => [
      {
        key: "details",
        label: i18n("SampleDetails.details"),
      },
      {
        key: "metadata",
        label: i18n("SampleDetails.metadata"),
      },
      {
        key: "files",
        label: i18n("SampleDetails.files"),
      },
      {
        key: "analyses",
        label: i18n("SampleDetails.analyses"),
      },
    ],
    []
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
              dispatch(removeSampleFromCartThunk());
              if (typeof refetch !== "undefined") refetch();
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
              dispatch(addSampleToCartThunk());
              if (typeof refetch !== "undefined") refetch();
            }}
          >
            {i18n("SampleDetailsViewer.addToCart")}
          </Button>
        )}
      </div>
      <HorizontalMenu
        defaultSelectedKeys={[tab]}
        onSelect={({ key }) => onMenuChange(key)}
        className="t-sample-viewer-nav"
        items={menuItems}
      />
    </div>
  );
}

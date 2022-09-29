import {
  ColumnHeightOutlined,
  ColumnWidthOutlined,
  DragOutlined,
  ZoomInOutlined,
  ZoomOutOutlined,
} from "@ant-design/icons";
import { Button, Divider, Tooltip } from "antd";
import React from "react";
import { useDispatch } from "react-redux";
import styled from "styled-components";
import { setZoomMode, zoomIn, zoomOut } from "../../redux/treeSlice";
import { useAppSelector } from "../../store";

const VerticalButtonBar = styled.div`
  display: flex;
  flex-direction: column;
  z-index: 4;
  position: absolute;
  right: 4px;
  bottom: 4px;
  user-select: none;
  background-color: var(--grey-1);
  box-shadow: rgba(0, 0, 0, 0.16) 0 1px 4px;
  .ant-divider-horizontal {
    margin: 0;
  }
`;

const zoomModeIcons: Record<number, JSX.Element> = {
  0: <DragOutlined />,
  1: <ColumnWidthOutlined />,
  2: <ColumnHeightOutlined />,
};

/**
 * React component to render zoom button capability for the phylogenetic tree.
 * @constructor
 */
export function ZoomButtons(): JSX.Element {
  const { zoomMode } = useAppSelector((state) => state.tree);
  const dispatch = useDispatch();

  const onZoomModeClick = () => {
    dispatch(setZoomMode((zoomMode + 1) % 3));
  };

  const onZoomInClick = () => {
    dispatch(zoomIn());
  };

  const onZoomOutClick = () => {
    dispatch(zoomOut());
  };

  return (
    <VerticalButtonBar>
      <Tooltip
        placement="left"
        title={i18n("visualization.phylogenomics.zoom.zoom-in")}
      >
        <Button icon={<ZoomInOutlined />} onClick={onZoomInClick} type="text" />
      </Tooltip>
      <Divider />
      <Tooltip
        placement="left"
        title={i18n("visualization.phylogenomics.zoom.zoom-toggle")}
      >
        <Button
          icon={zoomModeIcons[zoomMode]}
          onClick={onZoomModeClick}
          type="text"
        />
      </Tooltip>
      <Divider />
      <Tooltip
        placement="left"
        title={i18n("visualization.phylogenomics.zoom.zoom-out")}
      >
        <Button
          icon={<ZoomOutOutlined />}
          onClick={onZoomOutClick}
          type="text"
        />
      </Tooltip>
    </VerticalButtonBar>
  );
}

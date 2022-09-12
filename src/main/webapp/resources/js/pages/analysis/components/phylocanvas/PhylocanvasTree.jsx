import React, { useEffect } from "react";
import { useSelector, useDispatch } from "react-redux";
import { resize } from "../../redux/treeSlice";
import * as phylocanvas from "@phylocanvas/phylocanvas.gl";
import { Space } from "antd";
import { MetadataMenu } from "./MetadataMenu";
import { DownloadMenu } from "./DownloadMenu";
import { ZoomButtons } from "./ZoomButtons";
import { CollapsibleSidebar } from "./CollapsibleSidebar";
import { Legend } from "./Legend";

export function PhylocanvasTree({ height, width }) {
  const canvasRef = React.useRef();
  const treeRef = React.useRef();
  const { treeProps } = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const renderTree = (treeProps) => {
    treeRef.current = new phylocanvas.PhylocanvasGL(
      canvasRef.current,
      {
        ...treeProps,
        size: { height, width },
      },
      [phylocanvas.plugins.scalebar]
    );
  };

  React.useEffect(() => {
    if (treeRef.current) {
      treeRef.current.setProps({
        ...treeProps,
        size: {
          height,
          width,
        },
      });
    } else {
      renderTree(treeProps);
    }
  }, [renderTree, treeProps]);

  React.useEffect(() => {
    return () => {
      if (treeRef.current) {
        treeRef.current.destroy();
      }
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div ref={canvasRef}>
      <Space
        align="top"
        style={{
          position: "absolute",
          zIndex: 4,
          height: 0,
          right: 4,
          top: 4,
          userSelect: "none",
        }}
      >
        <MetadataMenu />
        <DownloadMenu treeRef={treeRef} />
      </Space>
      <ZoomButtons />
    </div>
  );
}

import React from "react";
// eslint-disable-next-line @typescript-eslint/ban-ts-comment
// @ts-ignore
import * as phylocanvas from "@phylocanvas/phylocanvas.gl";
import { Space } from "antd";
import { MetadataMenu } from "./MetadataMenu";
import { DownloadMenu } from "./DownloadMenu";
import { ZoomButtons } from "./ZoomButtons";
import PhylocanvasShapeDropDown from "./PhylocanvasShapeDropDown";
import { PhyloCanvas, TreeProperties } from "../../../../types/phylocanvas";
import { useAppSelector } from "../../store";
import { getTreeProps } from "../../redux/treeSlice";

interface PhyloCanvasTreeProps {
  height: number;
  width: number;
}

export function PhyloCanvasTree({ height, width }: PhyloCanvasTreeProps) {
  const canvasRef = React.useRef<null | HTMLDivElement>(null);
  const treeRef = React.useRef<null | PhyloCanvas>(null);
  const treeProps = useAppSelector(getTreeProps);

  const renderTree = (treeProps: TreeProperties) => {
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
  }, [height, renderTree, treeProps, width]);

  React.useEffect(() => {
    return () => {
      if (treeRef.current) {
        treeRef.current.destroy();
      }
    };
  }, []);

  return (
    <div ref={canvasRef}>
      <Space
        align="start"
        style={{
          position: "absolute",
          zIndex: 4,
          height: 0,
          right: 4,
          top: 4,
          userSelect: "none",
        }}
      >
        <PhylocanvasShapeDropDown />
        <MetadataMenu />
        <DownloadMenu treeRef={treeRef} />
      </Space>
      <ZoomButtons />
    </div>
  );
}

import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { resize } from "../../redux/treeSlice";
import * as phylocanvas from "@phylocanvas/phylocanvas.gl";
import { Space } from "antd";
import { MetadataMenu } from "./MetadataMenu";
import { DownloadMenu } from "./DownloadMenu";
import { ZoomButtons } from "./ZoomButtons";
import { CollapsibleSidebar } from "./CollapsibleSidebar";
import { Legend } from "./Legend";

export function PhylocanvasTree() {
  const canvasRef = React.useRef();
  const treeRef = React.useRef();
  const {treeProps} = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const renderTree = (treeProps) => {
    console.log(treeProps);
    treeRef.current = new phylocanvas.PhylocanvasGL(
      canvasRef.current,
      {
        ...treeProps,
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
      },
      [
        phylocanvas.plugins.scalebar
      ]
    );
  };

  const handleResize = () => {
    console.log()
    const {height, width} = canvasRef.current.parentElement.getBoundingClientRect();
    dispatch(resize({height, width}));
  }

  React.useEffect(() => {
    if (treeRef.current) {
      treeRef.current.setProps(treeProps);
    } else {
      renderTree(treeProps);
    }
  }, [renderTree, treeProps])

  React.useEffect(() => {
    window.addEventListener("resize", handleResize);

    return () => {
      if (treeRef.currnt) {
        treeRef.current.destroy();
      }
      window.removeEventListener("resize", handleResize);
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "row",
        alignItems: "start",
        width: "100%",
        height: "100%",
        flex: "1 1 auto",
        minHeight: 0,
      }}
    >
      <div
        style={{
          border: "1px solid lightgray",
          flex: "1 1 auto",
          minHeight: 0,
          minWidth: 0,
          width: "100%",
          height: "100%",
        }}
      >
        <div
          ref={canvasRef}
        >
          <Space
            align="top"
            style={{
              position: "absolute",
              zIndex: 4,
              height: 0,
              right: 4,
              top: 4,
              userSelect: "none"
            }}
          >
            <MetadataMenu />
            <DownloadMenu treeRef={treeRef}/>
          </Space>
          <ZoomButtons />

        </div>
      </div>
      <CollapsibleSidebar
        items={[
          {
            text: i18n("visualization.phylogenomics.sidebar.legend.title"),
            component: <Legend />
          }
        ]}
        onToggle={handleResize}
      />
    </div>
  );
}

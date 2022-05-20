import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { resize } from "../../redux/treeSlice";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button, Space } from "antd";
import { MetadataMenu } from "./MetadataMenu";

export function PhylocanvasTree() {
  const canvasRef = React.useRef();
  const treeRef = React.useRef();
  const {treeProps} = useSelector((state) => state.tree);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const renderTree = (treeProps) => {
    console.log(treeProps);
    treeRef.current = new PhylocanvasGL(
      canvasRef.current,
      {
        ...treeProps,
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
      },
      []
    );
  };

  const downloadTree = () => {
    const blob = treeRef.current.exportSVG();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.style.display = "none";
    link.href = url;
    link.setAttribute("download", `tree.svg`);
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  };

  React.useEffect(() => {
    if (treeRef.current) {
      treeRef.current.setProps(treeProps);
    } else {
      renderTree(treeProps);
    }
  }, [renderTree, treeProps])

  React.useEffect(() => {
    function handleResize() {
      const {height, width} = canvasRef.current.parentElement.getBoundingClientRect();
      dispatch(resize({height, width}));
    }

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
        flexDirection: "column",
        alignItems: "stretch",
        width: "100%",
        height: "100%",
        flex: "1 1 auto",
        minHeight: "0",
      }}
    >
      <div
        ref={canvasRef}
        style={{border: "1px solid lightgray"}}
      >
        <div
          style={{
            flex: "0 1 auto",
            display: "flex",
            flexDirection: "row-reverse",
            position: "absolute",
            zIndex: "4",
            height: 0,
            right: "4px",
            top: "20px",
            userSelect: "none"
          }}
        >
          <Space>
            <MetadataMenu />
            <Button onClick={downloadTree}>
              {i18n("visualization.button.export.svg")}
            </Button>
          </Space>
        </div>
      </div>
    </div>
  );
}

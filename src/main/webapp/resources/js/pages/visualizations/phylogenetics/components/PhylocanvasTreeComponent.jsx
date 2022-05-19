import React from "react";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button } from 'antd';

export function PhylocanvasTreeComponent({source}) {
  const canvasRef = React.createRef();
  const treeRef = React.createRef();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  function renderTree() {
    treeRef.current = new PhylocanvasGL(
      canvasRef.current,
      {
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
        alignLabels: true,
        interactive: true,
        showLabels: true,
        showLeafLabels: true,
        source
      },
      []
    );
  }

  const downloadTree = () => {
    const blob = treeRef.current.exportSVG();
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.style.display = "none";
    link.href = url;
    link.setAttribute(
      'download',
      `tree.svg`,
    );
    document.body.appendChild(link);
    link.click();
    window.URL.revokeObjectURL(url);
  }

  React.useEffect(() => {
    function handleResize() {
      console.log("hello I just resized");
      treeRef.current.destroy();
      renderTree();
    }

    window.addEventListener("resize", handleResize);

    return () => window.removeEventListener("resize", handleResize);
  });

  React.useEffect(() => {
    renderTree();
  });

  return (
    <div style={{display: "flex", flexDirection: "column", alignItems: "stretch", width: "100%", margin: 16}}>
      <div style={{flex: "0 1 auto", display: "flex", flexDirection: "row-reverse"}}>
        <Button onClick={downloadTree}>{i18n("visualization.button.export.svg")}</Button>
      </div>
      <div style={{flex: "1 1 auto", minHeight: "0"}}>
        <div ref={canvasRef}/>
      </div>
    </div>
  );
}
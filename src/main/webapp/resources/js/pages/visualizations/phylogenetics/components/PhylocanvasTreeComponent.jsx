import React from "react";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button } from 'antd';

export function PhylocanvasTreeComponent({source}) {
  const canvasRef = React.createRef();
  const treeRef = React.createRef();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  function renderTree() {
    if (treeRef.current !== null) {
      treeRef.current.destroy();
    }
    treeRef.current = new PhylocanvasGL(
      canvasRef.current,
      {
        //size: phylocanvasComponentRef.current.parentElement?.getBoundingClientRect(),
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
        alignLabels: true,
        interative: true,
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
    window.addEventListener("resize", renderTree);
      return () => window.removeEventListener(renderTree);
  }, [canvasRef, renderTree]);

  React.useEffect(() => {
    renderTree();
  }, [canvasRef, renderTree, source]);

  return (
    <div style={{display: "flex", flexDirection: "column", alignItems: "stretch", width: "100%", margin: 16}}>
      <div style={{flex: "0 1 auto", display: "flex", flexDirection: "row-reverse"}}>
        <Button onClick={downloadTree}>{i18n("visualization.button.export.svg")}</Button>
      </div>
      <div style={{flex: "1 1 auto"}}>
        <div ref={canvasRef}/>
      </div>
    </div>
  );
}
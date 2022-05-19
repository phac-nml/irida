import React from "react";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button, Checkbox } from 'antd';
import { formatMetadata } from "../metadata-utilities";

export function PhylocanvasTreeComponent({source, metadata, fields: originalFields, templates}) {
  const canvasRef = React.createRef();
  const treeRef = React.createRef();
  const [fields, setFields] = React.useState(() => {
    return originalFields.reduce((prev, current) => ({...prev, [current]: true}), {})
  })
  const [blocks, setBlocks] = React.useState(originalFields);

  React.useEffect(() => {
    setBlocks(Object.keys(fields).filter(field => fields[field]));
  }, [fields]);

  React.useEffect(() => {
    if (treeRef.current) {
      treeRef.current.setProps({ blocks });
    }
  }, [blocks, treeRef])

  const renderTree = () => {
    treeRef.current = new PhylocanvasGL(
      canvasRef.current,
      {
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
        alignLabels: true,
        interactive: true,
        showLabels: true,
        showLeafLabels: true,
        nodeShape: "dot",
        showBlockHeaders: true,
        blocks,
        source,
        metadata: metadata !== null ? formatMetadata(metadata, blocks) : metadata
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
      treeRef.current.setProps({ size: canvasRef.current.parentElement?.getBoundingClientRect() })
    }

    window.addEventListener("resize", handleResize);
    renderTree();

    return () => {
      treeRef.current.destroy();
      window.removeEventListener("resize", handleResize);
    }
  });

  const handleChecked = (e, field) => {
    setFields({...fields, [field]: e.target.checked});
  }

  return (
    <div style={{display: "flex", flexDirection: "column", alignItems: "stretch", width: "100%", margin: 16}}>
      <div style={{flex: "0 1 auto", display: "flex", flexDirection: "row-reverse"}}>
        <Button onClick={downloadTree}>{i18n("visualization.button.export.svg")}</Button>
        <div>
          {Object.keys(fields).map(field => (<Checkbox key={field} checked={fields[field]} onChange={e => handleChecked(e, field)}>{field}</Checkbox> ))}
        </div>
      </div>
      <div style={{flex: "1 1 auto", minHeight: "0"}}>
        <div ref={canvasRef}/>
      </div>
    </div>
  );
}
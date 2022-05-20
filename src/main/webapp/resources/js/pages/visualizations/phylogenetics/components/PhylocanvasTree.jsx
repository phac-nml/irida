import React from "react";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button, Space } from "antd";
import { formatMetadata, fetchMetadataTemplateFields } from "../metadata-utilities";
import { MetadataMenu } from "./MetadataMenu";

export function PhylocanvasTree({
  source,
  metadata,
  fields: originalFields,
  templates,
}) {
  const canvasRef = React.useRef();
  const treeRef = React.useRef();
  const [fields, setFields] = React.useState(() => {
    return originalFields.reduce(
      (prev, current) => ({ ...prev, [current]: true }),
      {}
    );
  });
  const allFields = originalFields.reduce(
      (prev, current) => ({ ...prev, [current]: true }),
      {}
    );
  const noFields = originalFields.reduce(
      (prev, current) => ({ ...prev, [current]: false }),
      {}
    );

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
        blocks: originalFields,
        source,
        metadata:
          metadata !== null ? formatMetadata(metadata, originalFields) : metadata,
      },
      []
    );
  };

  React.useEffect(() => {
    // only update visible metadata fields after tree has initialized
    // i.e. ignore on first initialization of fields
    if (treeRef.current) {
      treeRef.current.setProps({ blocks: Object.keys(fields).filter((field) => fields[field]) });
    }
  }, [fields]);

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
    function handleResize() {
      treeRef.current.setProps({
        size: canvasRef.current.parentElement?.getBoundingClientRect(),
      });
    }

    window.addEventListener("resize", handleResize);
    renderTree();

    return () => {
      treeRef.current.destroy();
      window.removeEventListener("resize", handleResize);
    };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const handleFieldChecked = (event, field) => {
    event.stopPropagation();
    setFields({ ...fields, [field]: event.target.checked });
  };

  const handleSelectAllChecked = (event, checked) => {
    event.stopPropagation();
    if (checked) {
      setFields(allFields);
    } else {
      setFields(noFields);
    }
  };

  const handleTemplateChange = (templateIdx) => {
    if (templateIdx === -1) {
      setFields(allFields);
    } else {
      if ("fields" in templates[templateIdx]) {
        setFields({...noFields, ...templates[templateIdx]["fields"]});
      } else {
        fetchMetadataTemplateFields(templates[templateIdx], fields).then((templateFields) => {
          templates[templateIdx].fields = templateFields;
          setFields({...noFields, ...templateFields});
        })
      }
    }
  };

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        alignItems: "stretch",
        width: "100%",
        margin: 16,
      }}
    >
      <div
        style={{
          flex: "0 1 auto",
          display: "flex",
          flexDirection: "row-reverse",
        }}
      >
        <Space>
          <MetadataMenu
            fields={fields}
            templates={templates}
            allSelected={JSON.stringify(fields) === JSON.stringify(allFields)}
            onTemplateChange={handleTemplateChange}
            onFieldChecked={handleFieldChecked}
            onSelectAllChange={handleSelectAllChecked}
          />
          <Button onClick={downloadTree}>
            {i18n("visualization.button.export.svg")}
          </Button>
        </Space>
      </div>
      <div style={{ flex: "1 1 auto", minHeight: "0" }}>
        <div ref={canvasRef} />
      </div>
    </div>
  );
}

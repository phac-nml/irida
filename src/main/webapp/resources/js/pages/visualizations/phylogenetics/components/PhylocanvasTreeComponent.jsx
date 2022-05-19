import React from "react";
import PhylocanvasGL from "@phylocanvas/phylocanvas.gl";
import { Button, Popover, Select, Space, Switch, Typography } from "antd";
import { FilterOutlined } from "@ant-design/icons";
import { formatMetadata } from "../metadata-utilities";
const { Option } = Select;

export function PhylocanvasTreeComponent({
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

  const handleChecked = (event, field, checked) => {
    event.stopPropagation();
    setFields({ ...fields, [field]: checked });
  };

  const fetchMetadataTemplateFields = async (templateIdx) => {
    let data = await templates[templateIdx]["callback"]();
    let templateFields = {};

    if (data.fields) {
      templateFields = data.fields.filter(field => field in fields).reduce(
        (prev, current) => ( { ...prev, [current]: true }),
        {}
      );
    }

    return templateFields;
  };

  const handleTemplateChange = (templateIdx) => {
    if (templateIdx === -1) {
      setFields(allFields);
    } else {
      if ("fields" in templates[templateIdx]) {
        setFields({...noFields, ...templates[templateIdx]["fields"]});
      } else {
        fetchMetadataTemplateFields(templateIdx).then((templateFields) => {
          templates[templateIdx]["fields"] = templateFields;
          setFields({...noFields, ...templateFields});
        })
      }
    }
  };

  const metadataTemplateOptions = [];
  metadataTemplateOptions.push(<Option key="template-default" value={-1}>{i18n("visualization.phylogenomics.select-template.all-field")}</Option>);
  for (let i=0; i<templates?.length; i++) {
    metadataTemplateOptions.push(<Option key={`template-${i}`} value={i}>{templates[i]["label"]}</Option>);
  }

  const metadataMenu = (
    <Space direction="vertical">
      <Select defaultValue={-1} style={{ width: 150 }} onChange={handleTemplateChange}>
        {metadataTemplateOptions}
      </Select>
      {Object.keys(fields)?.map((field) => (
        <div
          key={field}
          style={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center"
          }}
        >
          <Typography.Text strong>{field}</Typography.Text>
          <Switch
            checked={fields[field]}
            onChange={(checked, event) => handleChecked(event, field, checked)}
            size="small"
          />
        </div>
      ))}
    </Space>
  );

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
          <Popover content={metadataMenu} placement="bottomRight">
            <Button shape="circle" icon={<FilterOutlined />} />
          </Popover>
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

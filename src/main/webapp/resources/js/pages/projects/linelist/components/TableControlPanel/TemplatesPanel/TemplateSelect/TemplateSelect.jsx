import React from "react";
import { Select } from "antd";
import { TemplateSelectOption } from "./TemplateSelectOption";
import {
  HelpPopover,
  PopoverContents,
} from "../../../../../../../components/popovers/index";
import { SaveTemplateButton } from "./SaveTemplateButton";
import { SPACE_XS } from "../../../../../../../styles/spacing";

const { Option } = Select;

/*
The internationalized content of the help popover describing
what a template is and how to use it.
 */
const content = (
  <React.Fragment>
    <p>{i18n("linelist.templates.Popover.content")}</p>
    <p>{i18n("linelist.templates.Popover.description")}</p>
  </React.Fragment>
);

/**
 * Component to render an [antd Select]{@link https://ant.design/components/select/}
 * component to select a specific Metadata template.
 */
export function TemplateSelect(props) {
  const { current, templates, useTemplate } = props;

  return (
    <>
      <label style={{ color: "#707171", marginBottom: SPACE_XS }}>
        {i18n("linelist.templates.title")}
        <HelpPopover
          content={<PopoverContents contents={content} />}
          title={i18n("linelist.templates.Popover.title")}
        />
      </label>
      <div style={{ width: 240 }}>
        <Select
          style={{ width: 165, marginRight: SPACE_XS }}
          disabled={templates.length === 1}
          value={current}
          onSelect={useTemplate}
        >
          {templates.map((template, index) => (
            <Option key={template.id} value={index} title={template.name}>
              <TemplateSelectOption
                {...props}
                index={index}
                template={template}
              />
            </Option>
          ))}
        </Select>
        <SaveTemplateButton
          showSaveModal={props.showSaveModal}
          current={current}
          templates={templates}
          saveTemplate={props.saveTemplate}
        />
      </div>
    </>
  );
}

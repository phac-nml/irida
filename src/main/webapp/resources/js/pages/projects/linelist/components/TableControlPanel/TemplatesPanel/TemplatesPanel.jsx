import React from "react";
import PropTypes from "prop-types";
import { TemplateSelect } from "./TemplateSelect/TemplateSelect";
import styled from "styled-components";
import { grey5 } from "../../../../../../styles/colors";
import { SaveTemplateButton } from "./SaveTemplate";
import {
  HelpPopover,
  PopoverContents
} from "../../../../../../components/popovers";
import { ANT_DESIGN_FONT_FAMILY } from "../../../../../../styles/fonts";
import { SPACE_XS } from "../../../../../../styles/spacing";

const Wrapper = styled.div`
  height: 75px;
  border-bottom: 1px solid ${grey5};
  padding: 1rem;
`;

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
 * This component is responsible for rendering all components that handle
 * user interaction with selecting and saving templates.
 */
export class TemplatesPanel extends React.Component {
  showSaveModal = () => {
    this.setState({ visible: true });
  };

  constructor(props) {
    super(props);
  }

  render() {
    const { templates, current } = this.props;
    const template = templates[current];

    return (
      <Wrapper>
        <label
          style={{
            color: "#707171",
            fontSize: 14,
            fontWeight: 300,
            fontFamily: ANT_DESIGN_FONT_FAMILY,
            display: "block",
            marginBottom: SPACE_XS
          }}
        >
          {i18n("linelist.templates.title")}
          <HelpPopover
            content={<PopoverContents contents={content} />}
            title={i18n("linelist.templates.Popover.title")}
          />
        </label>
        <div
          style={{
            display: "flex",
            alignItems: "flex-end"
          }}
        >
          <TemplateSelect {...this.props} showSaveModal={this.showSaveModal} />
          <SaveTemplateButton
            disabled={template.modified.length === 0}
            template={template}
            {...this.props}
          />
        </div>
      </Wrapper>
    );
  }
}

TemplatesPanel.propTypes = {
  current: PropTypes.number.isRequired,
  saveTemplate: PropTypes.func.isRequired,
  templates: PropTypes.array.isRequired,
  modified: PropTypes.object
};

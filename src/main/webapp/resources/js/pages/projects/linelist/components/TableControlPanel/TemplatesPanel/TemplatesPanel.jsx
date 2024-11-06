import React from "react";
import { TemplateSelect } from "./TemplateSelect/TemplateSelect";
import styled from "styled-components";
import { grey5 } from "../../../../../../styles/colors";

const Wrapper = styled.div`
  height: 75px;
  border-bottom: 1px solid ${grey5};
  padding: 1rem;
`;

/**
 * This component is responsible for rendering all components that handle
 * user interaction with selecting and saving templates.
 */
export class TemplatesPanel extends React.Component {
  constructor(props) {
    super(props);
  }

  render() {
    const { templates, current } = this.props;

    return (
      <Wrapper>
        <TemplateSelect {...this.props} />
      </Wrapper>
    );
  }
}

import React from "react";
import styled from "styled-components";
import { Spin } from "antd";
import { blue1, blue3 } from "../../../../../../styles/colors";
import { SPACE_MD } from "../../../../../../styles/spacing";

const Wrapper = styled.div`
  border-radius: 4px;
  border: 2px solid ${blue3};
  background-color: ${blue1};
  font-size: 1.5rem;
  padding: 2rem;
`;

export const Overlay = ({ text }) => (
    <Wrapper>
      <Spin style={{ marginRight: SPACE_MD }} />
      <span>{text}</span>
    </Wrapper>);

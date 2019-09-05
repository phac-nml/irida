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

export const Overlay = ({ text }) => {
  if (typeof window.PAGE.i18n[text] === "undefined") {
    throw new Error(
      `Expected 'window.PAGE.i18n[${text}]' to be present on the page.`
    );
  }
  return (
    <Wrapper>
      <Spin style={{ marginRight: SPACE_MD }} />
      <span>{window.PAGE.i18n[text]}</span>
    </Wrapper>
  );
};

import React from "react";
import styled from "styled-components";
import { Breadcrumb } from "antd";
import { grey2, grey4, grey8 } from "../../styles/colors";
import { SPACE_MD, SPACE_XS } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { HomeTwoTone } from "@ant-design/icons";

const BreadCrumbs = styled(Breadcrumb)`
  background-color: ${grey2};
  padding: ${SPACE_XS} 15px;
  justify-content: flex-start;
  border-bottom: 1px solid ${grey4};
  margin-bottom: ${SPACE_MD};
`;

export function Breadcrumbs({ crumbs = [] }) {
  return crumbs.length ? (
    <BreadCrumbs>
      <Breadcrumb.Item>
        <a href={setBaseUrl("")}>
          <HomeTwoTone style={{ color: grey8 }} />
        </a>
      </Breadcrumb.Item>
      {crumbs.map(crumb => (
        <Breadcrumb.Item key={crumb.label}>
          <a href={crumb.url}>{crumb.label}</a>
        </Breadcrumb.Item>
      ))}
    </BreadCrumbs>
  ) : null;
}

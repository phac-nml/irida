import React from "react";
import { Breadcrumb } from "antd";
import { grey2, grey8 } from "../../styles/colors";
import { SPACE_XS } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { BORDERED_LIGHT } from "../../styles/borders";
import { IconHome } from "../icons/Icons";

/**
 * React component to render ant design Breadrumbs to pages that contain
 * a server set variable `breadcrumbs`
 *
 * @param {array} crumbs - list of links to render to the page.
 * @returns {*}
 * @constructor
 */
export function Breadcrumbs({ crumbs = [] }) {
  return crumbs?.length ? (
    <Breadcrumb
      style={{
        backgroundColor: grey2,
        padding: `${SPACE_XS} 45px`,
        justifyContent: "flex-start",
        borderBottom: BORDERED_LIGHT,
      }}
    >
      <Breadcrumb.Item>
        <a href={setBaseUrl("")}>
          <IconHome style={{ color: grey8 }} />
        </a>
      </Breadcrumb.Item>
      {crumbs.map((crumb) => (
        <Breadcrumb.Item key={crumb.label}>
          <a href={crumb.url}>{crumb.label}</a>
        </Breadcrumb.Item>
      ))}
    </Breadcrumb>
  ) : null;
}

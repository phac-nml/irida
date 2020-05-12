import React from "react";
import { Spin } from "antd";
import { FONT_COLOR_INFO, FONT_SIZE_SMALL } from "../styles/fonts";

/**
 * React component to be used when React Suspense is used to load a page
 * @param {string} text loading message
 * @returns {*}
 * @constructor
 */
export function PageLoadingIndicator({ text }) {
  return (
    <div
      style={{
        height: "100%",
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
      }}
    >
      <span style={{ fontSize: FONT_SIZE_SMALL, color: FONT_COLOR_INFO }}>
        <Spin /> {text}
      </span>
    </div>
  );
}

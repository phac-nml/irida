import React from "react";
import { SPACE_MD } from "../../../../styles/spacing";

/**
 * React component to allow children to scroll on th y axis.
 * @param children
 * @constructor
 */
export default function ScrollableSection({
  children,
}: {
  children: JSX.Element;
}): JSX.Element {
  return (
    <section
      style={{
        paddingLeft: SPACE_MD,
        height: "100%",
        overflowY: "auto",
      }}
    >
      {children}
    </section>
  );
}

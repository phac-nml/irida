import React from "react";
import Layout from "@theme/Layout";
import useBaseUrl from "@docusaurus/useBaseUrl";

const JavaDocPage = () => {
  const ref = React.useRef();
  const [height, setHeight] = React.useState("0px");
  const onLoad = () => {
    setHeight(ref.current.contentWindow.document.body.scrollHeight + "px");
  };
  return (
    <Layout>
      <iframe
        ref={ref}
        onLoad={onLoad}
        id="javadocFrame"
        src={useBaseUrl("/apidocs/")}
        width="100%"
        height={height}
        scrolling="no"
        frameBorder="0"
      ></iframe>
    </Layout>
  );
};

export default JavaDocPage;

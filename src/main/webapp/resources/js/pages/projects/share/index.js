import { Table } from "antd";
import React from "react";
import { render } from "react-dom";

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareSamples() {
  const [samples, setSamples] = React.useState();

  /*
  CURRENTLY THIS IS JUST TO HAVE SOMETHING ON THE PAGE AND SHOW THAT THE SAMPLES
  GET HERE.  DON'T WORRY TOO MUCH ABOUT LOOKING THROUGH THIS YET.
   */

  React.useEffect(() => {
    const stringData = window.sessionStorage.getItem("share");
    const data = JSON.parse(stringData);

    setSamples(data.samples);
  }, []);

  return (
    <Table
      loading={!samples}
      dataSource={samples}
      rowKey={(sample) => `sample-${sample.id}`}
      columns={[{ title: "Name", dataIndex: "name" }]}
    />
  );
}

render(<ShareSamples />, document.querySelector("#root"));

import { Row } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import { SharedSamplesList } from "./SharedSamplesList";

export function ShareSamples({ sampleIds = [] }) {
  const { originalSamples } = useSelector((state) => state.shareReducer);
  const [existing, setExisting] = React.useState([]);
  const [samples, setSamples] = React.useState([]);

  React.useEffect(() => {
    if (sampleIds) {
      const newExists = [];
      const newSamples = [];
      originalSamples.forEach((sample) => {
        if (sampleIds.includes(sample.id)) {
          newExists.push(sample);
        } else {
          newSamples.push(sample);
        }
      });
      setSamples(newSamples);
      setExisting(newExists);
    }
  }, [originalSamples, sampleIds]);

  return (
    <Row gutter={[16, 16]}>
      <SharedSamplesList list={samples} />
      <SharedSamplesList list={existing} />
    </Row>
  );
}

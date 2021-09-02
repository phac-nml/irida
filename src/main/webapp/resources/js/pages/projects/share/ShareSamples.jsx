import { Alert, Col, Row } from "antd";
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

  const showExisting = !!samples.length && !!existing.length;
  const space = showExisting ? { md: 12, xs: 24 } : { xs: 24 };

  return (
    <Row gutter={[16, 16]}>
      <Col {...space}>
        {samples.length ? (
          <SharedSamplesList list={samples} title={"Samples ready to copy"} />
        ) : (
          <Alert
            showIcon
            message={i18n("ShareSamples.ready")}
            description={
              "Copying these samples again will do absolutely nothing, please do something more worthwhile"
            }
          />
        )}
      </Col>
      {showExisting && (
        <Col {...space}>
          <SharedSamplesList
            list={existing}
            title={i18n("ShareSamples.exists")}
          />
        </Col>
      )}
    </Row>
  );
}

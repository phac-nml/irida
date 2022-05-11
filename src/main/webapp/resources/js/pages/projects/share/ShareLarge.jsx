import React from "react";
import { Row, Col, Alert, Progress } from "antd";
import { shareSamplesWithProject } from "../../../apis/projects/samples";

const MAX_SHARE_SIZE = 300;

/**
 * React component to render the progress of sharing large number of samples
 * @param {object} param
 * @returns React.Element
 */
export default function ShareLarge({
  samples,
  current,
  target,
  locked,
  remove,
  restrictions,
  onComplete,
}) {
  const [progress, setProgress] = React.useState(0);
  const numRequests = Math.ceil(samples.length / MAX_SHARE_SIZE);
  const promises = [];

  let shared = 0;
  function share(sampleIds) {
    promises.push(
      shareSamplesWithProject({
        sampleIds,
        locked,
        currentId: current,
        targetId: target,
        remove,
        restrictions: restrictions.map(({ restriction, id }) => ({
          restriction,
          identifier: id,
        })),
      }).then((response) => {
        shared += sampleIds.length;
        setProgress(Math.ceil((shared / samples.length) * 100));
        return response;
      })
    );
  }

  React.useEffect(() => {
    const ids = samples.map((sample) => sample.id);
    for (let i = 0; i < numRequests; i++) {
      const sampleIds = ids.slice(i * MAX_SHARE_SIZE, (i + 1) * MAX_SHARE_SIZE);
      share(sampleIds);
    }
    Promise.all(promises).then(() => {
      setTimeout(onComplete, 1000);
    });
  }, []);

  return (
    <Row gutter={[0, 16]}>
      <Col span={24}>
        <Alert
          type="info"
          message={i18n("ShareLarge.message", samples.length)}
          description={i18n("ShareLarge.description")}
        />
      </Col>
      <Col span={24}>
        <Progress percent={progress} status="active" />
      </Col>
    </Row>
  );
}

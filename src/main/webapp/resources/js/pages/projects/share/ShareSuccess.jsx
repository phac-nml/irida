import { Button, Result } from "antd";
import React from "react";
import {micromark} from 'micromark'
import { setBaseUrl } from "../../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/projects`);

function SingleMoved({ project, sample, extra }) {
  return (
    <Result
      extra={extra}
      className="t-move-single"
      status="success"
      title={micromark(i18n("ShareSuccess.move.title.single"))}
      subTitle={micromark(i18n(
            "ShareSuccess.move.subTitle.single",
            sample.name,
            project.label))
      }
    />
  );
}

function SingleShared({ project, sample, extra }) {
  return (
    <Result
      extra={extra}
      className="t-share-single"
      status="success"
      title={
        <ReactMarkdown className="t-success-title">
          {i18n("ShareSuccess.share.title.single")}
        </ReactMarkdown>
      }
      subTitle={
        <ReactMarkdown>
          {i18n(
            "ShareSuccess.share.subTitle.single",
            sample.name,
            project.label
          )}
        </ReactMarkdown>
      }
    />
  );
}

function MultipleMoved({ project, count, extra }) {
  return (
    <Result
      extra={extra}
      className="t-move-multiple"
      status="success"
      title={
        <ReactMarkdown className="t-success-title">
          {i18n("ShareSuccess.move.title.plural")}
        </ReactMarkdown>
      }
      subTitle={
        <ReactMarkdown>
          {i18n("ShareSuccess.move.subTitle.plural", count, project.label)}
        </ReactMarkdown>
      }
    />
  );
}

function MultipleShared({ project, count, extra }) {
  return (
    <Result
      extra={extra}
      className="t-share-multiple"
      status="success"
      title={
        <ReactMarkdown className="t-success-title">
          {i18n("ShareSuccess.share.title.plural")}
        </ReactMarkdown>
      }
      subTitle={
        <ReactMarkdown>
          {i18n("ShareSuccess.share.subTitle.plural", count, project.label)}
        </ReactMarkdown>
      }
    />
  );
}

/**
 * React component to show the successful completion of sharing/moving samples
 * @param message
 * @param removed
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSuccess({ removed, project, samples, currentProject }) {
  const single = samples.length === 1;

  const extra = [
    <Button key="return" href={`${BASE_URL}/${currentProject}`}>
      {i18n("ShareSuccess.link.samples")}
    </Button>,
    <Button
      type="primary"
      key="goto"
      href={`${BASE_URL}/${project.identifier}`}
    >
      {i18n("ShareSuccess.link.goto", project.label)}
    </Button>,
  ];

  // Single samples
  if (single && removed) {
    return <SingleMoved project={project} sample={samples[0]} extra={extra} />;
  } else if (single) {
    return <SingleShared project={project} sample={samples[0]} extra={extra} />;
  }

  // Multiple Samples,
  if (removed) {
    return (
      <MultipleMoved project={project} count={samples.length} extra={extra} />
    );
  }
  return (
    <MultipleShared project={project} count={samples.length} extra={extra} />
  );
}

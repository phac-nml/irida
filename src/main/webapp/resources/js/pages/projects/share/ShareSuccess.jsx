import { Button, Result } from "antd";
import React from "react";
import ReactMarkdown from "react-markdown";
import { setBaseUrl } from "../../../utilities/url-utilities";

function SingleMoved({ project, sample, extra }) {
  return (
    <Result
      extra={extra}
      className="t-move-single"
      status="success"
      title={
        <ReactMarkdown>{i18n("ShareSuccess.move.title.single")}</ReactMarkdown>
      }
      subTitle={
        <ReactMarkdown>
          {i18n(
            "ShareSuccess.move.subTitle.single",
            sample.name,
            project.label
          )}
        </ReactMarkdown>
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
        <ReactMarkdown>{i18n("ShareSuccess.move.title.single")}</ReactMarkdown>
      }
      subTitle={
        <ReactMarkdown>
          {i18n(
            "ShareSuccess.move.subTitle.single",
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
        <ReactMarkdown>{i18n("ShareSuccess.move.title.plural")}</ReactMarkdown>
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
        <ReactMarkdown>{i18n("ShareSuccess.share.title.plural")}</ReactMarkdown>
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
    <Button key="return" href={setBaseUrl(`/projects/${currentProject}`)}>
      {i18n("ShareSuccess.link.samples")}
    </Button>,
    <Button
      type="primary"
      key="goto"
      href={setBaseUrl(`projects/${project.identifier}`)}
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

import { Result } from "antd";
import React from "react";
import ReactMarkdown from "react-markdown";

function SingleMoved({ project, sample }) {
  return (
    <Result
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

function SingleShared({ project, sample }) {
  return (
    <Result
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

function MultipleMoved({ project, count }) {
  return (
    <Result
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

function MultipleShared({ project, count }) {
  return (
    <Result
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
export function ShareSuccess({ removed, project, samples }) {
  const single = samples.length === 1;

  // Single samples
  if (single && removed) {
    return <SingleMoved project={project} sample={samples[0]} />;
  } else if (single) {
    return <SingleShared project={project} sample={samples[0]} />;
  }

  // Multiple Samples,
  if (removed) {
    return <MultipleMoved project={project} count={samples.length} />;
  }
  return <MultipleShared project={project} count={samples.length} />;
}

import { Button, Result } from "antd";
import React from "react";
import { micromark } from "micromark";
import { setBaseUrl } from "../../../utilities/url-utilities";

const BASE_URL = setBaseUrl(`/projects`);

function SingleMoved({ project, sample, extra }) {
  return (
    <Result
      extra={extra}
      className="t-move-single"
      status="success"
      title={micromark(i18n("ShareSuccess.move.title.single"))}
      subTitle={micromark(i18n("ShareSuccess.move.subTitle.single", sample.name, project.label))}
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
        <div
          className="t-success-title"
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.share.title.single")),
          }}
        />
      }
      subTitle={
        <div
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.share.subTitle.single", sample.name, project.label)),
          }}
        />
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
        <div
          className="t-success-title"
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.move.title.plural")),
          }}
        />
      }
      subTitle={
        <div
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.move.subTitle.plural", count, project.label)),
          }}
        />
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
        <div
          className="t-success-title"
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.share.title.plural")),
          }}
        />
      }
      subTitle={
        <div
          dangerouslySetInnerHTML={{
            __html: micromark(i18n("ShareSuccess.share.subTitle.plural", count, project.label)),
          }}
        />
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
    </Button>
  ];

  if (single) {
    return removed ? (
      <SingleMoved project={project} sample={samples[0]} extra={extra} />
    ) : (
      <SingleShared project={project} sample={samples[0]} extra={extra} />
    );
  }
  return removed ? (
    <MultipleMoved project={project} count={samples.length} extra={extra} />
  ) : (
    <MultipleShared project={project} count={samples.length} extra={extra} />
  );
}

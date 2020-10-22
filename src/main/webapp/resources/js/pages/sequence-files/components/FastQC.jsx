/*
 * This file is responsible for displaying the
 * tabs required
 */

import React, { Suspense, useEffect, useState } from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { Link, Location, Router } from "@reach/router";
import { Menu } from "antd";
import { ContentLoading } from "../../../components/loader";

import { SPACE_MD } from "../../../styles/spacing"
import { getFastQCDetails } from "../../../apis/files/sequence-files";

const FastQCDetails = React.lazy(() => import("./FastQCDetails"));
const FastQCImages = React.lazy(() => import("./FastQCImages"));
const OverRepresentedSequences = React.lazy(() => import("./OverRepresentedSequences"));

export default function FastQC() {
  const [loading, setLoading] = useState(true);
  const [file, setFile] = useState({});
  // 4 different urls map to this page
  const url1 = `projects/${window.PAGE.projectId}/samples/${window.PAGE.sampleId}/sequenceFiles/${window.PAGE.seqObjectId}/file/${window.PAGE.seqFileId}/summary`;
  const url2 = `projects/${window.PAGE.projectId}/samples/${window.PAGE.sampleId}/sequenceFiles/${window.PAGE.seqObjectId}/file/${window.PAGE.seqFileId}`;
  const url3 = `sequenceFiles/${window.PAGE.seqObjectId}/file/${window.PAGE.seqFileId}/summary`;
  const url4 = `sequencingRuns/${window.PAGE.runId}/sequenceFiles/${window.PAGE.seqObjectId}/file/${window.PAGE.seqFileId}/summary`;

  const urlMatch = window.location.href.match(url1) || window.location.href.match(url2) || window.location.href.match(url3) || window.location.href.match(url4);
  const DEFAULT_URL = setBaseUrl(urlMatch[0]);

  const [selectedKeys, setSelectedKeys] = React.useState(() => {
    // Create a regex from the string with an additional string at the end
    const regexStr = urlMatch[0] + "/(\\w*)?";
    const urlRegex = new RegExp(regexStr, "i");

    const urlMatchRegex = window.location.href.match(urlRegex);
    // Grab the key if it exists from the match array otherwise default to 'images'
    return urlMatchRegex !== null ? urlMatchRegex[1] : "images";
  });

  useEffect(() => {
    getFastQCDetails(window.PAGE.seqObjectId, window.PAGE.seqFileId).then(({ sequenceFile }) => {
      setFile(sequenceFile);
      setLoading(false);
    });
  }, []);

  return (
    <PageWrapper title={file.fileName}>
      <Location>
        {() => {
          return (
            <Menu
              mode="horizontal"
              selectedKeys={[selectedKeys]}
            >
              <Menu.Item key="images">
                <Link to={`${DEFAULT_URL}/images`}
                      onClick={() => setSelectedKeys("images")}>
                  FastQC Images
                </Link>
              </Menu.Item>
              <Menu.Item key="overrepresented">
                <Link to={`${DEFAULT_URL}/overrepresented`}
                      onClick={() => setSelectedKeys("overrepresented")}>
                  Overrepresented Sequences
                </Link>
              </Menu.Item>
              <Menu.Item key="details">
                <Link to={`${DEFAULT_URL}/details`}
                      onClick={() => setSelectedKeys("details")}>
                  Details
                </Link>
              </Menu.Item>
            </Menu>
          );
        }}
      </Location>
      <Suspense fallback={<ContentLoading/>}>
        <Router style={{paddingTop: SPACE_MD}}>
          <FastQCImages path={`${DEFAULT_URL}/images`} default
                        key="images"/>
          <OverRepresentedSequences path={`${DEFAULT_URL}/overrepresented`}
                                    key="overrepresented"/>
          <FastQCDetails path={`${DEFAULT_URL}/details`} key="details"/>
        </Router>
      </Suspense>
    </PageWrapper>
  );
}

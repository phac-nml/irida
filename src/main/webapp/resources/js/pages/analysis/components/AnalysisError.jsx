import React, { useContext, useEffect, useState } from "react";
import { AnalysisContext } from "../../../contexts/AnalysisContext";
import { getI18N } from "../../../utilities/i18n-utilties";
import { Button } from "antd";

import { getJobErrors } from "../../../apis/analysis/analysis";

import { formatDate } from "../../../utilities/date-utilities";

export function AnalysisError() {
  const { analysisContext } = useContext(AnalysisContext);
  const [jobErrors, setJobErrors] = useState({});

  useEffect(() => {
    getJobErrors(analysisContext.analysis.identifier).then(data => {
      setJobErrors(data);
    });
  }, []);

  return (
    <>
      {"jobErrors" in jobErrors ? (
        <div>
          <h3>
            Job Error -{" "}
            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].toolName}{" "}
            (
            {
              jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                .toolVersion
            }
            ) - Exit Code{" "}
            {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1].exitCode}
          </h3>
          <table className="table table-bordered table-condensed">
            <thead>
              <tr>
                <th className="col-md-3">Attribute</th>
                <th className="col-md-9">Value</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <th>Created Date</th>
                <td>
                  <p>
                    {formatDate({
                      date:
                        jobErrors["jobErrors"][
                          jobErrors["jobErrors"].length - 1
                        ].createdDate
                    })}
                  </p>
                </td>
              </tr>
              <tr>
                <th>Updated Date</th>
                <td>
                  <p>
                    {formatDate({
                      date:
                        jobErrors["jobErrors"][
                          jobErrors["jobErrors"].length - 1
                        ].updatedDate
                    })}
                  </p>
                </td>
              </tr>
              <tr>
                <th>Command Line</th>
                <td>
                  <pre style={{ whiteSpace: "pre-wrap" }}>
                    {
                      jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                        .commandLine
                    }
                  </pre>
                </td>
              </tr>
              <tr>
                <th>Exit Code</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .exitCode
                  }
                </td>
              </tr>
              <tr>
                <th>Tool Id</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .toolId
                  }
                </td>
              </tr>
              <tr>
                <th>Tool Name</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .toolName
                  }
                </td>
              </tr>
              <tr>
                <th>Tool Version</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .toolVersion
                  }
                </td>
              </tr>
              <tr>
                <th>Tool Description</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .toolDescription
                  }
                </td>
              </tr>
              <tr>
                <th>Provenance Id</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .provenanceId
                  }
                </td>
              </tr>
              <tr>
                <th>Provenance UUID</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .provenanceUUID
                  }
                </td>
              </tr>
              <tr>
                <th>History Id</th>
                <td>
                  <Button
                    type="link"
                    style={{ paddingLeft: 0 }}
                    href={`${jobErrors["galaxyUrl"]}/histories/view?id=${
                      jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                        .historyId
                    }`}
                  >
                    {
                      jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                        .historyId
                    }
                  </Button>
                </td>
              </tr>
              <tr>
                <th>Job Id</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .jobId
                  }
                </td>
              </tr>
              <tr>
                <th>Identifier</th>
                <td>
                  {
                    jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                      .identifier
                  }
                </td>
              </tr>
            </tbody>
          </table>

          {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
            .standardError ? (
            <div>
              <h3>Standard Error</h3>
              <pre style={{ whiteSpace: "pre-wrap" }}>
                {
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .standardError
                }
              </pre>
            </div>
          ) : null}

          {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
            .standardOutput ? (
            <div>
              <h3>Standard Output</h3>
              <pre style={{ whiteSpace: "pre-wrap" }}>
                {
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .standardOutput
                }
              </pre>
            </div>
          ) : null}

          {jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
            .parameters ? (
            <div>
              <h3>Galaxy Parameters</h3>
              <pre style={{ whiteSpace: "pre-wrap" }}>
                {
                  jobErrors["jobErrors"][jobErrors["jobErrors"].length - 1]
                    .parameters
                }
              </pre>
            </div>
          ) : null}
        </div>
      ) : (
        "No Job Error Information Available"
      )}
    </>
  );
}

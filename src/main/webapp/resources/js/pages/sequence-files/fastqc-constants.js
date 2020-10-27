/*
 * Constants file for the FastQC react components
 */

export const seqObjId = window.location.href.match(/sequenceFiles\/(\d+)/) !== null ? window.location.href.match(/sequenceFiles\/(\d+)/)[1] : null;
export const seqFileId = window.location.href.match(/file\/(\d+)/) !== null ? window.location.href.match(/file\/(\d+)/)[1] : null;

// Get the ids which are required for the url matching below
const projId = window.location.href.match(/projects\/(\d+)/) !== null ? window.location.href.match(/projects\/(\d+)/)[1] : null;
const sampleId = window.location.href.match(/samples\/(\d+)/) !== null ? window.location.href.match(/samples\/(\d+)/)[1] : null;
const runId = window.location.href.match(/sequencingRuns\/(\d+)/) !== null ? window.location.href.match(/sequencingRuns\/(\d+)/)[1] : null;

// URLs that map to this page
const url1 = `/projects/${projId}/samples/${sampleId}/sequenceFiles/${seqObjId}/file/${seqFileId}/summary`;
const url2 = `/projects/${projId}/samples/${sampleId}/sequenceFiles/${seqObjId}/file/${seqFileId}`;
const url3 = `/samples/${sampleId}/sequenceFiles/${seqObjId}/file/${seqFileId}/summary`
const url4 = `/sequenceFiles/${seqObjId}/file/${seqFileId}/summary`;
const url5 = `/sequencingRuns/${runId}/sequenceFiles/${seqObjId}/file/${seqFileId}/summary`;

export const urlMatch =
  window.location.pathname.match(url1) ||
  window.location.pathname.match(url2) ||
  window.location.pathname.match(url3) ||
  window.location.pathname.match(url4) ||
  window.location.pathname.match(url5);
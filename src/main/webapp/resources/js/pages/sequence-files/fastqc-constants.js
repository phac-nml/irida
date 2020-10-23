/*
 * Constants file for the FastQC react components
 */

export const projId = window.location.href.match(/projects\/(\d+)/) !== null ? window.location.href.match(/projects\/(\d+)/)[1] : null;

export const sampleId = window.location.href.match(/samples\/(\d+)/) !== null ? window.location.href.match(/samples\/(\d+)/)[1] : null;

export const seqObjId = window.location.href.match(/sequenceFiles\/(\d+)/) !== null ? window.location.href.match(/sequenceFiles\/(\d+)/)[1] : null;

export const seqFileId = window.location.href.match(/file\/(\d+)/) !== null ? window.location.href.match(/file\/(\d+)/)[1] : null;

export const runId = window.location.href.match(/sequencingRuns\/(\d+)/) !== null ? window.location.href.match(/sequencingRuns\/(\d+)/)[1] : null;
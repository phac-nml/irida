import { getContextPath } from "../utilities/url-utilities";

export const CONTEXT_PATH = getContextPath();

export const ROUTE_HOME = CONTEXT_PATH;
export const ROUTE_PROJECTS_PERSONAL = `${CONTEXT_PATH}/projects`;
export const ROUTE_PROJECTS_ALL = `${CONTEXT_PATH}/projects/all`;
export const ROUTE_PROJECTS_SYNC = `${CONTEXT_PATH}/projects/synchronize`;

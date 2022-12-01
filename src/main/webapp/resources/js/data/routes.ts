import { getContextPath } from "../utilities/url-utilities";

export const CONTEXT_PATH = getContextPath();

export const ROUTE_ADMIN = `${CONTEXT_PATH}/admin`;
export const ROUTE_ANALYSES = `${CONTEXT_PATH}/analysis`;
export const ROUTE_ANALYSES_ALL = `${CONTEXT_PATH}/analysis/all`;
export const ROUTE_ANALYSES_OUTPUT = `${CONTEXT_PATH}/analysis/user/analysis-outputs`;
export const ROUTE_ANNOUNCEMENTS = `${CONTEXT_PATH}/announcements/user/list`;
export const ROUTE_CART = `${CONTEXT_PATH}/cart`;
export const ROUTE_HOME = CONTEXT_PATH;
export const ROUTE_LOGOUT = `${CONTEXT_PATH}/logout`;
export const ROUTE_PROJECTS_ALL = `${CONTEXT_PATH}/projects/all`;
export const ROUTE_PROJECTS_PERSONAL = `${CONTEXT_PATH}/projects`;
export const ROUTE_PROJECTS_SYNC = `${CONTEXT_PATH}/projects/synchronize`;
export const ROUTE_REMOTE_API = `${CONTEXT_PATH}/remote_api`;
export const ROUTE_SEARCH = `${CONTEXT_PATH}/search`;
export const ROUTE_SEQUENCING_RUNS = `${CONTEXT_PATH}/sequencing-runs`;
export const ROUTE_USERS = `${CONTEXT_PATH}/users`;
export const ROUTE_USER_GROUPS = `${CONTEXT_PATH}/groups`;

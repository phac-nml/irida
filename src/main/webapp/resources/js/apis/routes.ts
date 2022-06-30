const CONTEXT_PATH = document.documentElement.dataset.context || "/";

function formatUrl(path: TemplateStringsArray, ...keys: (number | string)[]) {
  return function (urlParams?: Record<string, string | number>): string {
    let url = CONTEXT_PATH;
    if (urlParams) {
      const temp = path.slice();
      keys.forEach((key, i) => {
        temp[i] = temp[i] + urlParams[key];
      });
      url += temp.join("");
    } else {
      url += path;
    }

    return url;
  };
}

export const activities_project_route = formatUrl`ajax/activities/project`;
export const activities_recent_route = formatUrl`ajax/activities/all`;
export const activities_user_route = formatUrl`ajax/activities/user`;

export const admin_statistics_route = formatUrl`ajax/statistics/basic`;
export const admin_statistics_users_route = formatUrl`ajax/statistics/users`;
export const admin_statistics_samples_route = formatUrl`ajax/statistics/samples`;
export const admin_statistics_analyses_route = formatUrl`ajax/statistics/analyses`;
export const admin_statistics_projects_route = formatUrl`ajax/statistics/projects`;

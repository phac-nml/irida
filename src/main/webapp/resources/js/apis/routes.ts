const CONTEXT_PATH = document.documentElement.dataset.context;

function formatUrl(url: TemplateStringsArray, ...keys: (number | string)[]) {
  return function (data: { [key: string]: string | number }) {
    const temp = url.slice();
    console.log(temp);
    keys.forEach((key, i) => {
      temp[i] = temp[i] + data[key];
    });
    return `${CONTEXT_PATH}${temp.join("")}`;
  };
}

export const projectSamplesUrl = formatUrl`/projects/${"projectId"}`;
